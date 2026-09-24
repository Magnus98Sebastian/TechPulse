package com.example.techpulse.ui.presentation.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techpulse.ui.screens.BookmarksScreen

/**
 * Route-Composable für den Lesezeichen-Screen.
 *
 * Dient als Bindeglied zwischen dem [BookmarksViewModel] und der zustandslosen [BookmarksScreen]-UI.
 * Beobachtet den [BookmarksUiState] lebenszyklusbewusst mittels [collectAsStateWithLifecycle]
 * und leitet Benutzerinteraktionen (Lesezeichen umschalten, Likes vergeben, Post auswählen) an das ViewModel weiter.
 *
 * @param onPostClick Callback bei Ausführung eines Klicks auf einen Beitrag zur Navigation in die Detailansicht. Übergibt die [String]-ID des Beitrags.
 * @param viewModel Das injected [BookmarksViewModel] zur Verwaltung der Lesezeichendaten.
 */
@Composable
fun BookmarksRoute(
    onPostClick: (String) -> Unit,
    viewModel: BookmarksViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BookmarksScreen(
        uiState = uiState,
        onBookmarkToggle = { bookmark -> viewModel.onBookmarkToggle(bookmark) },
        onPostClick = { postId -> onPostClick(postId) },
        onRepoClick = { repoId -> onPostClick(repoId.toString()) },
        onLikeClick = { postId, post -> viewModel.onLikeClick(postId, post) }
    )
}
