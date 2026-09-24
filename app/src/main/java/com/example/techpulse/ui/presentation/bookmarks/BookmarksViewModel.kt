package com.example.techpulse.ui.presentation.bookmarks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.local.BookmarkDocument
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.domain.Post
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel zur Verwaltung der gespeicherten Lesezeichen und der Interaktionen auf dem [BookmarksRoute]-Screen.
 *
 * Kombiniert reaktiv gespeicherte Lesezeichen mit lokalen und Remote-Like-Zuständen aus dem [TechPulseRepository],
 * um den aktuellen [BookmarksUiState] bereitzustellen.
 *
 * @property repository Das Repository für den Zugriff auf Lesezeichen, Likes und Live-Updates.
 */
class BookmarksViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    /**
     * Der reaktive [StateFlow] des [BookmarksUiState].
     *
     * Kombiniert kontinuierlich die Streams für Lesezeichen, gelikte Beitrags-IDs und Live-Like-Zähler,
     * berechnet die gepatchten [BookmarkDocument]-Zustände und gibt sie als [BookmarksUiState.Success] aus.
     * Fängt Fehler ab und wandelt diese in [BookmarksUiState.Error] um.
     */
    val uiState: StateFlow<BookmarksUiState> = combine(
        repository.bookmarks,
        repository.getLikedPostIds(),
        repository.getLivePostLikeCounts()
    ) { bookmarksList, likedIds, firestoreCounts ->
        val updatedBookmarks = bookmarksList.map { bookmark ->
            val isLiked = likedIds.contains(bookmark.id)
            val firestoreCount = firestoreCounts[bookmark.id]

            val finalLikeCount = when {
                firestoreCount != null && firestoreCount > 0 -> firestoreCount
                isLiked && !bookmark.isLiked -> bookmark.likeCount + 1
                !isLiked && bookmark.isLiked -> (bookmark.likeCount - 1).coerceAtLeast(0)
                else -> bookmark.likeCount
            }

            bookmark.copy(
                isLiked = isLiked,
                likeCount = finalLikeCount
            )
        }
        BookmarksUiState.Success(bookmarks = updatedBookmarks) as BookmarksUiState
    }
        .catch { throwable ->
            emit(BookmarksUiState.Error(message = throwable.localizedMessage ?: "Unbekannter Fehler"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookmarksUiState.Loading
        )

    /**
     * Schaltet den Lesezeichen-Status für das übergebene [BookmarkDocument] um.
     *
     * @param bookmark Das Lesezeichen-Dokument, das hinzugefügt oder entfernt werden soll.
     */
    fun onBookmarkToggle(bookmark: BookmarkDocument) {
        viewModelScope.launch {
            repository.toggleBookmark(bookmark)
        }
    }

    /**
     * Schaltet den Like-Status für den angegebenen Beitrag um und aktualisiert die Like-Anzahl.
     *
     * @param postId Die eindeutige ID des Beitrags.
     * @param post Das [Post]-Objekt zur Bestimmung der bisherigen Like-Anzahl.
     */
    fun onLikeClick(postId: String, post: Post) {
        viewModelScope.launch {
            try {
                repository.toggleLike(postId = postId, initialLikeCount = post.likeCount)
            } catch (e: Exception) {
                Log.e("BookmarksViewModel", "Fehler beim toggleLike", e)
            }
        }
    }
}