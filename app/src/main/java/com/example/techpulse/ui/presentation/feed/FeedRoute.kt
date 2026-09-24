package com.example.techpulse.ui.presentation.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techpulse.ui.navigation.Screen
import com.example.techpulse.ui.screens.FeedScreen

/**
 * Route-Composable für den News-Feed-Screen.
 *
 * Dient als Bindeglied zwischen dem [FeedViewModel] und der zustandslosen [FeedScreen]-UI.
 * Beobachtet den [FeedUiState] lebenszyklusbewusst mittels [collectAsStateWithLifecycle]
 * und leitet Benutzerinteraktionen (Post-Auswahl, Likes, Lesezeichen, Pull-to-Refresh) an das ViewModel weiter.
 *
 * @param viewModel Das injected [FeedViewModel] zur Verwaltung der Feed-Daten und -Aktionen.
 * @param onPostClick Callback bei Ausführung eines Klicks auf einen Beitrag zur Navigation in die Detailansicht. Übergibt die [String]-ID des Beitrags.
 * @param modifier Der [Modifier] zur externen Layout-Konfiguration.
 */
@Composable
fun FeedRoute(
    viewModel: FeedViewModel,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedScreen(
        uiState = uiState,
        onLoadMore = {
            viewModel.loadingMorePosts()
        },
        onPostClick = { postId ->
            onPostClick(postId)
        },
        onLikeClick = { articleId, post ->
            viewModel.toggleLike(articleId, post)
        },
        onBookmarkClick = { post ->
            viewModel.toggleBookmark(post)
        },
        onRefresh = {
            viewModel.loadFeed()
        },
        modifier = modifier
    )
}