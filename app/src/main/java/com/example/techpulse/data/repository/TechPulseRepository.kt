package com.example.techpulse.data.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.techpulse.data.local.BookmarkDocument
import com.example.techpulse.data.mapper.toDomainModel
import com.example.techpulse.data.remote.TechPulseApi
import com.example.techpulse.domain.Article
import com.example.techpulse.domain.Comment
import com.example.techpulse.domain.RepositoryItem
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.coroutines.resume
import com.example.techpulse.data.mapper.toPost
import com.example.techpulse.domain.Post

/**
 * Zentrales Repository zur Verwaltung aller Datenströme und Datenoperationen der Anwendung.
 *
 * Kombiniert Remote-Schnittstellen ([TechPulseApi]), Cloud-Datenbanken ([FirebaseFirestore]),
 * Benutzerauthentifizierung ([FirebaseAuth]) sowie Medien-Uploads (Cloudinary SDK).
 *
 * @property api Der Retrofit-Service für den Zugriff auf Dev.to und GitHub APIs.
 * @property firestore Die Instanz der Cloud Firestore Datenquelle.
 * @property auth Die Firebase Authentication Instanz für Benutzerverwaltungen.
 */
class TechPulseRepository(
    private val api: TechPulseApi,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    /** Referenz auf die allgemeine `bookmarks`-Collection in Firestore. */
    private val bookmarksCollection = firestore.collection("bookmarks")

    /**
     * Ruft die Firebase-UID des aktuell angemeldeten Benutzers ab.
     *
     * @return Die eindeutige UID oder `null`, wenn kein Benutzer angemeldet ist.
     */
    val currentUserId: String?
        get() = auth.currentUser?.uid

    /**
     * Stellt sicher, dass eine gültige Benutzer-ID vorhanden ist.
     * Meldet den Benutzer anonym an, falls bisher keine Sitzung existiert.
     *
     * @return Die Firebase-UID des bestehenden oder neu erstelleten anonymen Benutzers.
     * @throws IllegalStateException Wenn der anonyme Anmeldevorgang fehlschlägt.
     */
    suspend fun ensureAnonymousUser(): String {
        val existingUid = currentUserId
        if (existingUid != null) return existingUid

        val authResult = auth.signInAnonymously().await()
        return authResult.user?.uid ?: throw IllegalStateException("Anonymer Login fehlgeschlagen")
    }

    /**
     * Überprüft, ob das Benutzerprofil in Firestore vollständig eingerichtet ist (insb. Benutzername).
     *
     * @return `true`, wenn ein Profil existiert und ein nicht-leerer Benutzername gesetzt ist, sonst `false`.
     */
    suspend fun isUserSetupCompleted(): Boolean {
        val uid = currentUserId ?: return false
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.exists() && !snapshot.getString("username").isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Echtzeit-[Flow] aller Lesezeichen des aktuellen Benutzers aus Firestore.
     *
     * Registriert einen SnapshotListener auf die benutzerspezifische Lesezeichen-Collection
     * und emittiert bei jeder Änderung die aktualisierte Liste an [BookmarkDocument]-Objekten.
     */
    val bookmarks: Flow<List<BookmarkDocument>> = callbackFlow {

        val userId = currentUserId ?: ensureAnonymousUser()

        val userBookmarksRef = firestore.collection("users")
            .document(userId)
            .collection("bookmarks")
        val listenerRegistration = userBookmarksRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(BookmarkDocument::class.java)?.copy(id = doc.id)
                }
                trySend(items)
            }
        }
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Hilfsmethode zum Abrufen des Lesezeichen-[Flow]s.
     *
     * @return Ein [Flow] mit der Liste gespeicherter [BookmarkDocument]-Instanzen.
     */
    fun getBookmarkedPosts(): Flow<List<BookmarkDocument>> = bookmarks

    /**
     * Sucht nach GitHub-Repositorys über die [TechPulseApi].
     *
     * @param query Der Suchbegriff.
     * @param page Die Seitennummer für Paginierung (Standard: `1`).
     * @return Ein [Result] mit der Liste der gefundenen [RepositoryItem]-Objekte oder der aufgetretenen Exception.
     */
    suspend fun searchRepositories(query: String, page: Int = 1): Result<List<RepositoryItem>> {
        return try {
            val response = api.searchRepositories(query = query, page = page)
            val domainList = response.items.map { it.toDomainModel() }
            Log.d("TechPulseAPI", "Repositories geladen (Seite $page): ${domainList.size} Einträge")
            Result.success(domainList)
        } catch (e: Exception) {
            Log.e("TechPulseAPI", "Fehler bei searchRepositories für Query: '$query', Seite $page", e)
            Result.failure(e)
        }
    }

    /**
     * Lädt die neuesten Artikel von Dev.to für den Haupt-Feed.
     *
     * @return Ein [Result] mit einer Liste von [Article]-Domänenobjekten.
     */
    suspend fun getFeedArticles(page: Int = 1, perPage: Int = 20): Result<List<Post>> = runCatching {
        return try {
            val response = api.getDevToArticles(page = page, perPage = perPage)
            val articles = response.map { dto -> dto.toDomainModel().toPost() }
            Log.d("TechPulseAPI", "Feed erfolgreich geladen: ${articles.size} Artikel")
            Result.success(articles)
        } catch (e: Exception) {
            Log.e("TechPulseAPI", "Fehler bei getFeedArticles", e)
            Result.failure(e)
        }
    }

    /**
     * Lädt und kombiniert Kommentare für einen Artikel aus zwei Quellen:
     * 1. Lokale Benutzerkommentare aus Cloud Firestore.
     * 2. Offizielle Artikelkommentare von der Dev.to API.
     *
     * @param articleId Die Kennung des Artikels.
     * @return Ein [Result] mit der zusammengeführten Liste von [Comment]-Objekten.
     */
    suspend fun getCommentsForArticle(articleId: String): Result<List<Comment>> {
        return runCatching {
            val localComments = try {
                val firestoreSnapshot = firestore.collection("posts")
                    .document(articleId)
                    .collection("comments")
                    .get()
                    .await()

                firestoreSnapshot.documents.mapNotNull { doc ->
                    val text = doc.getString("text") ?: return@mapNotNull null
                    val author = doc.getString("authName") ?: "Anonym"
                    val avatarUrl = doc.getString("userAvatarUrl") ?: ""

                    Comment(
                        id = doc.id,
                        username = author,
                        contentText = text,
                        timestamp = "Gerade eben",
                        userAvatarUrl = avatarUrl
                    )
                }
            } catch (e: Exception) {
                Log.e("TechPulse", "Fehler beim Laden der Firestore-Kommentare für Post $articleId", e)
                emptyList()
            }

            val devToComments = try {
                api.getArticleComments(articleId).map { it.toDomainModel() }
            } catch (e: Exception) {
                Log.e("TechPulse", "Fehler beim Laden der API-Kommentare für Post $articleId", e)
                emptyList()
            }

            localComments + devToComments
        }
    }

    /**
     * Schaltet den Lesezeichen-Status eines Artikels/Postings im Profil des aktuellen Benutzers um.
     * Löscht das Lesezeichen, falls bereits vorhanden, oder speichert es andernfalls.
     *
     * @param bookmark Das zu speichernde oder zu entfernende [BookmarkDocument].
     */
    suspend fun toggleBookmark(bookmark: BookmarkDocument) {
        val userId = currentUserId ?: ensureAnonymousUser()

        val userBookmarkRef = firestore.collection("users")
            .document(userId)
            .collection("bookmarks")
            .document(bookmark.id)

        val exists = isBookmarkedOneShot(bookmark.id)

        if (exists) {
            userBookmarkRef.delete().await()
        } else {
            userBookmarkRef.set(bookmark).await()
        }
    }

    /**
     * Schaltet den Lesezeichen-Status für ein GitHub-Repository um.
     * Konvertiert das [RepositoryItem] dafür in ein [BookmarkDocument].
     *
     * @param repo Das betreffende GitHub-Repository.
     */
    suspend fun  toggleRepoBookmark(repo: RepositoryItem) {
        val bookmark = BookmarkDocument(
            id = repo.id.toString(),
            title = repo.name,
            description = repo.description ?: "",
            imageUrl = repo.ownerAvatarUrl ?: "",
            userAvatarUrl = repo.ownerAvatarUrl,
            sourceName = repo.ownerName,
            createdAt = System.currentTimeMillis(),
            type = "REPOSITORY"
        )
        toggleBookmark(bookmark)
    }

    /**
     * Beobachtet in Echtzeit, ob ein spezifisches Dokument im globalen Lesezeichen-Verzeichnis existiert.
     *
     * @param id Die ID des Dokuments.
     * @return Ein [Flow] mit `true`, falls das Dokument existiert, sonst `false`.
     */
    fun isBookmarked(id: String): Flow<Boolean> = callbackFlow {
        val listenerRegistration = bookmarksCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot != null && snapshot.exists())
            }
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Führt eine einmalige (One-Shot) Prüfung durch, ob ein Lesezeichen in der
     * benutzerspezifischen Sammlung des aktuellen Benutzers existiert.
     *
     * @param id Die Kennung des Lesezeichens.
     * @return `true`, wenn das Lesezeichen existiert, sonst `false`.
     */
    suspend fun isBookmarkedOneShot(id: String): Boolean {
        val userId = currentUserId ?: return false
        return firestore.collection("users")
            .document(userId)
            .collection("bookmarks")
            .document(id)
            .get()
            .await()
            .exists()
    }

    /**
     * Prüft in Echtzeit, ob der aktuelle Benutzer einen bestimmten Beitrag mit „Gefällt mir“ markiert hat.
     *
     * @param postId Die ID des Beitrags.
     * @return Ein [Flow] von [Boolean]. Emittiert `false`, falls kein Benutzer angemeldet ist.
     */
    fun isLiked(postId: String): Flow<Boolean> = callbackFlow {
        val uid: String? = currentUserId

        if (uid == null) {
            trySend(false)
            close()
        } else {
            val docRef = firestore.collection("users")
                .document(uid)
                .collection("likes")
                .document(postId)

            val listener = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(false)
                } else {
                    trySend(snapshot != null && snapshot.exists())
                }
            }

            awaitClose { listener.remove() }
        }
    }

    /**
     * Schaltet den Like-Status eines Beitrags mittels Firestore-Transaktion atomic um.
     * Aktualisiert synchron den Like-Eintrag des Benutzers sowie den Gesamtzähler (`likeCount`) am Post-Dokument.
     *
     * @param postId Die ID des Beitrags.
     * @param initialLikeCount Ursprünglicher Like-Zähler als Fallback, falls der Beitrag noch nicht in Firestore angelegt ist.
     */
    suspend fun toggleLike(postId: String, initialLikeCount: Int = 0) {
        val userId = currentUserId ?: ensureAnonymousUser()

        val userLikeRef = firestore.collection("users")
            .document(userId)
            .collection("likes")
            .document(postId)

        val postRef = firestore.collection("posts").document(postId)

        firestore.runTransaction { transaction ->
            val userLikeSnapshot = transaction.get(userLikeRef)
            val postSnapshot = transaction.get(postRef)

            val isCurrentlyLiked = userLikeSnapshot.exists()
            val firestoreCount = postSnapshot.getLong("likeCount")?.toInt()
            val baseCount = if (firestoreCount == null || firestoreCount == 0) {
                initialLikeCount
            } else {
                firestoreCount
            }

            if (isCurrentlyLiked) {
                transaction.delete(userLikeRef)
                val newCount = (baseCount - 1).coerceAtLeast(0)
                transaction.set(postRef, mapOf("likeCount" to newCount), SetOptions.merge())
            } else {
                transaction.set(userLikeRef, mapOf("createdAt" to Timestamp.now()))
                val newCount = baseCount + 1
                transaction.set(postRef, mapOf("likeCount" to newCount), SetOptions.merge())
            }
        }.await()
    }

    /**
     * Stellt einen Live-[Flow] bereit, der die aktuellen Like-Zahlen aller registrierten Posts als Map emittiert.
     *
     * @return Ein [Flow] einer Map von `Post-ID -> Like-Anzahl`.
     */
    fun getLivePostLikeCounts(): Flow<Map<String, Int>> = callbackFlow {
        val listener = firestore.collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyMap())
                } else {
                    val countsMap = snapshot.documents.associate { doc ->
                        doc.id to (doc.getLong("likeCount")?.toInt() ?: 0)
                    }
                    trySend(countsMap)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Ruft den hinterlegten Benutzernamen des aktuellen Benutzers aus Firestore ab.
     *
     * @return Der Benutzername oder `null`, falls nicht zugreifbar.
     */
    suspend fun getUsername(): String? {
        val userId = currentUserId ?: return null
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.getString("username")
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Speichert den angegebenen Benutzernamen im Profil des aktuellen Benutzers.
     * Generiert automatisch einen zufälligen Benutzernamen, falls die Eingabe leer ist.
     *
     * @param name Der gewünschte Benutzername.
     */
    suspend fun saveUsername(name: String) {
        val userId = currentUserId ?: ensureAnonymousUser()
        val finalName = name.ifBlank { generateRandomUsernameWithUuid() }

        firestore.collection("users").document(userId)
            .set(
                mapOf(
                    "userId" to userId,
                    "username" to finalName,
                    "createdAt" to Timestamp.now()
                ),
                SetOptions.merge()
            )
            .await()
    }

    /**
     * Ruft die URL des Profilbilds (Avatar) des aktuellen Benutzers ab.
     *
     * @return Die Avatar-URL als String oder `null`.
     */
    suspend fun getUserAvatarUrl(): String? {
        val userId = currentUserId ?: return null
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.getString("avatarUrl")
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Speichert oder aktualisiert die Avatar-Bild-URL des aktuellen Benutzers in Firestore.
     *
     * @param avatarUrl Die einzutragende Bild-URL.
     */
    suspend fun saveUserAvatarUrl(avatarUrl: String) {
        val userId = currentUserId ?: ensureAnonymousUser()

        firestore.collection("users").document(userId)
            .set(
                mapOf(
                    "avatarUrl" to avatarUrl.trim()
                ),
                SetOptions.merge()
            )
            .await()
    }

    /**
     * Erzeugt einen zufälligen Fallback-Benutzernamen mit angehängter UUID-Kennung.
     *
     * @return Ein String im Format `"TechPulse-User#XXXXXX"`.
     */
    fun generateRandomUsernameWithUuid(): String {
        val uniqueId = UUID.randomUUID().toString().take(6)
        return "TechPulse-User#$uniqueId"
    }

    /**
     * Prüft, ob ein bestimmter Benutzername bereits von einem anderen Benutzer verwendet wird.
     *
     * @param username Der zu prüfende Benutzername.
     * @return `true`, wenn der Name bereits von einer anderen UID belegt ist, sonst `false`.
     */
    suspend fun isUsernameTaken(username: String): Boolean {
        val userId = currentUserId ?: return false

        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username.trim())
                .get()
                .await()

            querySnapshot.documents.any { doc -> doc.id != userId }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Fügt einem Post einen neuen Kommentar in Firestore hinzu und aktualisiert die lokale Kommentaranzahl des Beitrags.
     *
     * @param postId Die ID des Posts.
     * @param commentText Der Text des Kommentars.
     */
    suspend fun addComment(postId: String, commentText: String) {
        val userId = currentUserId ?: ensureAnonymousUser()
        val authName = getUsername() ?: generateRandomUsernameWithUuid()
        val avatarUrl = getUserAvatarUrl() ?: auth.currentUser?.photoUrl?.toString() ?: ""

        val commentData = mapOf(
            "text" to commentText,
            "userId" to userId,
            "authName" to authName,
            "userAvatarUrl" to avatarUrl,
            "createdAt" to Timestamp.now()
        )

        firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .add(commentData)
            .await()

        val allCommentsSnapshot = firestore.collection("posts")
            .document(postId)
            .collection("comments")
            .get()
            .await()

        val localCommentCount = allCommentsSnapshot.size()

        firestore.collection("posts")
            .document(postId)
            .set(
                mapOf("localCommentCount" to localCommentCount),
                SetOptions.merge()
            )
            .await()
    }

    /**
     * Ruft Detailinformationen eines Dev.to-Artikels ab und wandelt diese in das [Article]-Domänenmodell um.
     *
     * @param articleId Die Kennung des Artikels.
     * @return Ein [Result] mit dem aufbereiteten [Article]-Objekt.
     */
    suspend fun getArticleDetails(articleId: String): Result<Article> {
        return runCatching {
            val dto = api.getArticleDetails(articleId)
            dto.toDomainModel()
        }.onSuccess {
            Log.d("TechPulseAPI", "Artikel-Details geladen für ID: $articleId")
        }.onFailure { e ->
            Log.e("TechPulseAPI", "Fehler bei getArticleDetails für ID: $articleId", e)
        }
    }

    /**
     * Beobachtet die Live-Like-Anzahl eines spezifischen Posts aus Firestore.
     *
     * @param postId Die ID des Posts.
     * @return Ein [Flow] mit der aktuellen Anzahl an Likes (`Long`).
     */
    fun getPostDetailsFlow(postId: String): Flow<Long> = callbackFlow {
        val docRef = firestore.collection("posts").document(postId)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                trySend(0L)
            } else {
                val count = snapshot.getLong("likeCount") ?: 0L
                trySend(count)
            }
        }

        awaitClose { listener.remove() }
    }

    /**
     * Stellt einen Live-[Flow] mit allen Post-IDs bereit, die der aktuelle Benutzer mit „Gefällt mir“ markiert hat.
     *
     * @return Ein [Flow] mit einem Set von Post-ID-Strings.
     */
    fun getLikedPostIds(): Flow<Set<String>> = callbackFlow {
        val uid = currentUserId
        if (uid == null) {
            trySend(emptySet())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(uid)
            .collection("likes")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptySet())
                } else {
                    val ids = snapshot.documents.map { it.id }.toSet()
                    trySend(ids)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Lädt ein Profilbild über das Cloudinary MediaManager SDK hoch.
     * Wandelt die Callback-basierte API von Cloudinary in eine aussetzbare (suspend) Coroutine-Funktion um.
     *
     * @param imageUri Die lokale Uri der hochzuladenden Bilddatei.
     * @return Ein [Result] mit der sicheren HTTPS-URL (`secure_url`) des hochgeladenen Bildes.
     */
    suspend fun uploadAvatarToCloudinary(imageUri: Uri): Result<String> {
        return suspendCancellableCoroutine { continuation ->

            val requestId = MediaManager.get().upload(imageUri)
                .unsigned("techpulse_preset")
                .option("cloud_name", "ljmkqmbe")
                .option("folder", "avatars")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl != null) {
                            continuation.resume(Result.success(secureUrl))
                        } else {
                            continuation.resume(Result.failure(Exception("Keine URL empfangen")))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.e("CloudinaryError", "Code: ${error.code}, Msg: ${error.description}")
                        continuation.resume(Result.failure(Exception("Cloudinary [${error.code}]: ${error.description}")))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception("Upload neu terminiert")))
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                MediaManager.get().cancelRequest(requestId)
            }
        }
    }
}