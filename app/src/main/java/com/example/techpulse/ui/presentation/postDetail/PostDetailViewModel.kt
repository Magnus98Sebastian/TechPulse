package com.example.techpulse.ui.presentation.postDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.domain.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel zur Steuerung der Beitrags-Detailansicht ([PostDetailRoute]).
 *
 * Verwaltet das Laden vollständiger Artikeldetails und zugehöriger Kommentare aus dem [TechPulseRepository],
 * steuert das Erstellen neuer Kommentare und beobachtet den Live-Zustand bezüglich Lesezeichen und Likes.
 *
 * @property repository Das Repository für den Zugriff auf Artikel, Kommentare und Firestore-Live-Daten.
 */
class PostDetailViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)

    /** Der reaktive [StateFlow] des aktuellen [PostDetailUiState]. */
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    private var currentPostId: String? = null

    /**
     * Lädt die Kommentare für einen bestimmten Beitrag neu.
     *
     * @param postId Die eindeutige ID des Beitrags.
     * @param forceRefresh Bei `true` werden die Kommentare auch dann neu geladen, wenn die ID bereits übereinstimmt.
     */
    fun loadComments(postId: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && currentPostId == postId && _uiState.value !is PostDetailUiState.Error) return
        currentPostId = postId

        viewModelScope.launch {
            val result = repository.getCommentsForArticle(postId)
            result.onSuccess { newComments ->
                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        currentState.copy(comments = newComments)
                    } else {
                        PostDetailUiState.Success(comments = newComments)
                    }
                }
            }.onFailure { error ->
                _uiState.value = PostDetailUiState.Error(
                    error.message ?: "Fehler beim Laden der Kommentare"
                )
            }
        }
    }

    /**
     * Fügt dem angegebenen Beitrag einen neuen Kommentar hinzu.
     *
     * Validiert den Text, speichert den Kommentar im [repository], aktualisiert die Kommentarliste
     * sowie den Kommentarzähler des [Post]-Objekts in der UI optimistisch.
     *
     * @param postId Die ID des zu kommentierenden Beitrags.
     * @param commentText Der Inhalt des neuen Kommentars.
     */
    fun addComment(postId: String, commentText: String) {
        val cleanText = commentText.trim()
        if (cleanText.isBlank()) return

        viewModelScope.launch {
            try {
                println("DEBUG_COMMENT: addComment aufgerufen mit Text = $cleanText für Post $postId")
                repository.addComment(postId = postId, commentText = cleanText)
                println("DEBUG_COMMENT: Erfolgreich in Repository gespeichert, lade Kommentare neu...")

                loadComments(postId = postId, forceRefresh = true)

                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        val updatedPost = currentState.post?.copy(
                            commentCount = currentState.post.commentCount + 1
                        )
                        currentState.copy(post = updatedPost)
                    } else {
                        currentState
                    }
                }

            } catch (e: Exception) {
                println("DEBUG_COMMENT_ERROR: Fehler beim Senden: ${e.message}")
                e.printStackTrace()

                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        currentState.copy(errorMessage = "Fehler beim Senden: ${e.message}")
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    /**
     * Lädt die vollständigen Details sowie die Kommentare für den übergebenen Beitrag.
     *
     * @param postId Die ID des abzurufenden Beitrags.
     */
    fun loadPostDetail(postId: String) {
        if (currentPostId == postId && _uiState.value is PostDetailUiState.Success) return
        currentPostId = postId

        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading

            val articleResult = repository.getArticleDetails(postId)
            val commentsResult = repository.getCommentsForArticle(postId)

            articleResult.onSuccess { fullArticle ->
                val content = fullArticle.bodyText.ifBlank { fullArticle.description }

                val updatedPost = Post(
                    id = fullArticle.id,
                    username = fullArticle.sourceName ?: "Unbekannt",
                    userAvatarUrl = fullArticle.userAvatarUrl,
                    timestamp = fullArticle.publishedAt,
                    contentText = content ?: "",
                    imageUrl = fullArticle.imageUrl,
                    likeCount = if (fullArticle.publicReactionsCount > 0) fullArticle.publicReactionsCount else fullArticle.likeCount,
                    commentCount = fullArticle.commentCount,
                    isLiked = false,
                    isBookmarked = false
                )

                val commentsList = commentsResult.getOrDefault(emptyList())

                _uiState.value = PostDetailUiState.Success(
                    post = updatedPost,
                    comments = commentsList
                )
                observeBookmarkState(postId)
                observeLikeState(postId)
            }.onFailure { error ->
                _uiState.value = PostDetailUiState.Error(
                    error.message ?: "Fehler beim Laden des Posts"
                )
            }
        }
    }

    /**
     * Beobachtet den Lesezeichen-Zustand des Beitrags und hält den UI-State synchron.
     *
     * @param postId Die ID des zu beobachtenden Beitrags.
     */
    private fun observeBookmarkState(postId: String) {
        viewModelScope.launch {
            repository.isBookmarked(postId).collect { isBookmarked ->
                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        val currentPost = currentState.post
                        if (currentPost != null) {
                            currentState.copy(
                                post = currentPost.copy(isBookmarked = isBookmarked)
                            )
                        } else {
                            currentState
                        }
                    } else {
                        currentState
                    }
                }
            }
        }
    }

    /**
     * Beobachtet den Like-Zustand sowie die Live-Like-Anzahl des Beitrags aus Firestore und synchronisiert sie mit dem UI-State.
     *
     * @param postId Die ID des zu beobachtenden Beitrags.
     */
    private fun observeLikeState(postId: String) {
        viewModelScope.launch {
            repository.isLiked(postId).collect { isLiked ->
                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        val currentPost = currentState.post
                        if (currentPost != null) {
                            currentState.copy(post = currentPost.copy(isLiked = isLiked))
                        } else currentState
                    } else currentState
                }
            }
        }

        viewModelScope.launch {
            repository.getPostDetailsFlow(postId).collect { liveLikeCount ->
                val countAsInt = liveLikeCount.toInt()
                _uiState.update { currentState ->
                    if (currentState is PostDetailUiState.Success) {
                        val currentPost = currentState.post
                        if (currentPost != null) {
                            val updatedCount = if (countAsInt > 0) countAsInt else currentPost.likeCount
                            currentState.copy(post = currentPost.copy(likeCount = updatedCount))
                        } else currentState
                    } else currentState
                }
            }
        }
    }

    /**
     * Schaltet den Like-Status des angegebenen Beitrags um und synchronisiert ihn mit dem Backend.
     *
     * @param postId Die ID des Beitrags, für den das Like umgeschaltet werden soll.
     * @param post Das [Post]-Objekt zur Referenzierung des initialen Like-Zählers.
     */
    fun toggleLike(postId: String, post: Post) {
        viewModelScope.launch {
            try {
                repository.toggleLike(postId = postId, initialLikeCount = post.likeCount)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", "Fehler beim toggleLike", e)
            }
        }
    }
}


