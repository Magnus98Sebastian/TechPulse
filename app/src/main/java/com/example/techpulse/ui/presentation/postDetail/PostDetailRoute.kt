package com.example.techpulse.ui.presentation.postDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.techpulse.domain.Post
import com.example.techpulse.ui.screens.PostDetailScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Route-Composable für die Beitrags-Detailansicht.
 *
 * Verwaltet die Initialisierung des [PostDetailViewModel]s für die gegebene Beitrags-ID,
 * beobachtet den [PostDetailUiState] und verknüpft die UI-Events der [PostDetailScreen]
 * mit der Navigation und Logikschicht.
 *
 * @param post Das ursprüngliche [Post]-Objekt, das als Initialdaten übergeben wird.
 * @param navController Der [NavController] zur Handhabung von Navigationsaktionen (z. B. Zurücknavigieren).
 * @param onBookmarkClick Callback bei Klick auf die Lesezeichen-Schaltfläche. Übergibt den aktuellen [Post].
 * @param onLikeClick Callback bei Klick auf die Like-Schaltfläche.
 * @param viewModel Das per Dependency Injection (Koin) bereitgestellte [PostDetailViewModel].
 */
@Composable
fun PostDetailRoute(
    post: Post,
    navController: NavController,
    onBookmarkClick: (Post) -> Unit,
    onLikeClick: () -> Unit,
    viewModel: PostDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(post.id) {
        viewModel.loadPostDetail(postId = post.id)
    }
    val state = uiState
    val currentPost = if (state is PostDetailUiState.Success && state.post != null) {
        state.post
    } else {
        post
    }

    PostDetailScreen(
        post = post,
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onBookmarkClick = { onBookmarkClick(currentPost) },
        onLikeClick = onLikeClick,
        onSendComment = { text ->
                            println("DEBUG_NAV: Route empfängt Kommentar = $text")
                            viewModel.addComment(postId = post.id, commentText = text)
                        }
    )
}