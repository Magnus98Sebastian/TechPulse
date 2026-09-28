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
 * und leitet Benutzerinteraktionen an das ViewModel weiter.
 *
 * @param onPostClick Callback zur Navigation in die Detailansicht mit der Post- oder Repo-ID.
 * @param viewModel Das [BookmarksViewModel] zur Verwaltung der Lesezeichendaten.
 */
@Composable
fun BookmarksRoute(
    onPostClick: (String) -> Unit,
    viewModel: BookmarksViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BookmarksScreen(
        uiState = uiState,
        onBookmarkToggle = viewModel::onBookmarkToggle,
        onPostClick = onPostClick,
        onRepoClick = { repoId -> onPostClick(repoId.toString()) },
        onLikeClick = viewModel::onLikeClick
    )
}
