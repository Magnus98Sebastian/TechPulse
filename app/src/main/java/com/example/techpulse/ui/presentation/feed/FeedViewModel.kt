package com.example.techpulse.ui.presentation.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.mapper.toBookmarkDocument
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.domain.Article
import com.example.techpulse.domain.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.techpulse.data.mapper.toPost

/**
 * ViewModel zur Steuerung des Haupt-Feeds ([FeedRoute]) der TechPulse-Anwendung.
 *
 * Verwaltet das Laden und Zwischenspeichern von Nachrichten-Artikeln ([Article]) aus dem [TechPulseRepository],
 * transformiert diese dynamisch in [Post]-Domänenmodelle und führt kontinuierlich Lesezeichen-, Like-
 * sowie Live-Zählerstände aus Firestore zusammen.
 *
 * @property repository Das Repository für Datenzugriffe auf Artikel, Lesezeichen und Firestore-Events.
 */
class FeedViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)

    /** Der reaktive [StateFlow] des aktuellen [FeedUiState]. */
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _cachedArticles = MutableStateFlow<List<Post>>(emptyList())

    private var currentPage = 1
    private var isLastPage = false
    private var isLoadingMore = false

    init {
        Log.d("TechPulseAPI", "Hallo LogCat! Das Modul wurde geladen.")
        observeFeedAndBookmarks()
        loadFeed()
    }

    /**
     * Beobachtet reaktiv die geordneten Datenquellen für Artikel, Lesezeichen,
     * gelikte Beitrags-IDs sowie Firestore-Live-Zählerstände und führt sie zu einer Liste von [Post]-Objekten zusammen.
     */
    private fun observeFeedAndBookmarks() {
        viewModelScope.launch {
            combine(
                _cachedArticles,
                repository.getBookmarkedPosts(),
                repository.getLikedPostIds(),
                repository.getLivePostLikeCounts()
            ) { posts, bookmarkedPosts, likedIds, firestoreCounts ->
                val bookmarkedIds = bookmarkedPosts.map { it.id }.toSet()

                posts.map { post ->
                    val isLiked = likedIds.contains(post.id)
                    val firestoreCount = firestoreCounts[post.id]

                    val firestoreLikeCount = when {
                        firestoreCount != null && firestoreCount > 0 -> firestoreCount
                        isLiked -> post.likeCount + 1
                        else -> post.likeCount
                    }

                    post.copy(
                        likeCount = firestoreLikeCount,
                        isBookmarked = bookmarkedIds.contains(post.id),
                        isLiked = isLiked
                    )
                }
            }
                .catch { error ->
                    _uiState.value = FeedUiState.Error(error.message ?: "Fehler beim Laden")
                }
                .collect { updatedPosts ->
                    if (_cachedArticles.value.isNotEmpty()) {
                        _uiState.value = FeedUiState.Success(posts = updatedPosts)
                    }
                }
        }
    }

    /**
     * Lädt die neuesten Feed-Artikel über das [repository].
     *
     * Zeigt den Ladezustand an, sofern noch keine gecachten Artikel vorliegen,
     * und aktualisiert bei Erfolg den internen Puffer [_cachedArticles].
     */
    fun loadFeed() {
        currentPage = 1
        isLastPage = false
        viewModelScope.launch {
            if (_cachedArticles.value.isEmpty()) {
                _uiState.value = FeedUiState.Loading
            }

            repository.getFeedArticles(page = currentPage, perPage = 20)
                .onSuccess { post ->

                    _cachedArticles.value = post
                    _uiState.value = FeedUiState.Success(
                        posts = post,
                        isLoadingMore = false,
                    )
                }
                .onFailure { error ->
                    if (_cachedArticles.value.isEmpty()) {
                        _uiState.value = FeedUiState.Error(error.message ?: "Fehler beim Laden")
                    }
                }
        }
    }

    /**
     * Lädt die nächste Seite von Feed-Beiträgen für das unendliche Scrollen (Pagination) nach.
     *
     * Inkrementiert die Seitenzahl, fordert weitere Beiträge über das [repository] an
     * und hängt diese an die bestehende Liste in [_cachedArticles] an.
     */
    fun loadingMorePosts() {
        if(isLoadingMore || isLastPage || _uiState.value !is FeedUiState.Success) return

        isLoadingMore = true
        val currentPosts = _cachedArticles.value

        _uiState.value = FeedUiState.Success(
            posts = currentPosts,
            isLoadingMore = true
        )

        viewModelScope.launch {
            val nextPage = currentPage + 1

            repository.getFeedArticles(page = nextPage, perPage = 20)
                .onSuccess { newArticles ->
                    if(newArticles.isEmpty()) {
                        isLastPage = true
                        _uiState.value = FeedUiState.Success(
                            posts = currentPosts,
                            isLoadingMore = false
                        )
                    } else {
                        currentPage = nextPage
                        val newPosts = newArticles
                        val updatedList = currentPosts + newPosts
                        _cachedArticles.value = updatedList

                        _uiState.value = FeedUiState.Success(
                            posts = updatedList,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = FeedUiState.Success(
                        posts = currentPosts,
                        isLoadingMore = false
                    )
                }
            isLoadingMore = false
        }
    }

    /**
     * Schaltet den Lesezeichen-Status für den übergebenen Beitrag um.
     *
     * @param post Der [Post], der hinzugefügt oder aus den Lesezeichen entfernt werden soll.
     */
    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            repository.toggleBookmark(post.toBookmarkDocument())
        }
    }

    /**
     * Schaltet den Like-Status eines Beitrags um.
     *
     * Führt eine optimistische UI-Aktualisierung durch und synchronisiert den Zustand
     * anschließend asynchron über das Repository mit dem Backend.
     *
     * @param postId Die eindeutige ID des Beitrags.
     * @param post Das [Post]-Objekt zur Referenzierung der Ausgangswerte.
     */
    fun toggleLike(postId: String, post: Post) {
        viewModelScope.launch {

            _uiState.update { currentState ->
                if (currentState is FeedUiState.Success) {
                    val updatedArticles = currentState.posts.map { item ->
                        if (item.id == postId) {
                            val currentlyLiked = item.isLiked
                            val updatedCount = if (currentlyLiked) {
                                (item.likeCount - 1).coerceAtLeast(0)
                            } else {
                                item.likeCount + 1
                            }
                            item.copy(
                                isLiked = !currentlyLiked,
                                likeCount = updatedCount
                            )
                        } else {
                            item
                        }
                    }
                    currentState.copy(posts = updatedArticles)
                } else {
                    currentState
                }
            }

            try {
                repository.toggleLike(postId = postId, initialLikeCount = post.likeCount)
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Fehler beim toggleLike", e)
            }
        }
    }

    /**
     * Liefert ein [Post]-Objekt aus dem aktuellen Erfolgszustand anhand seiner ID.
     *
     * @param postId Die eindeutige ID des gesuchten Beitrags.
     * @return Das passende [Post]-Objekt oder `null`, falls der Beitrag nicht gefunden wurde oder der State nicht [FeedUiState.Success] ist.
     */
    fun getPostById(postId: String): Post? {
        val currentState = uiState.value
        return if (currentState is FeedUiState.Success) {
            currentState.posts.find { it.id == postId }
        } else {
            null
        }
    }
}