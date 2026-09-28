package com.example.techpulse.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.ui.presentation.bookmarks.BookmarksRoute
import com.example.techpulse.ui.presentation.bookmarks.BookmarksViewModel
import com.example.techpulse.ui.presentation.feed.FeedRoute
import com.example.techpulse.ui.presentation.feed.FeedViewModel
import com.example.techpulse.ui.presentation.postDetail.PostDetailRoute
import com.example.techpulse.ui.presentation.postDetail.PostDetailViewModel
import com.example.techpulse.ui.presentation.repoDetail.RepoDetailRoute
import com.example.techpulse.ui.presentation.repos.ReposRoute
import com.example.techpulse.ui.presentation.repos.ReposUiState
import com.example.techpulse.ui.presentation.repos.ReposViewModel
import com.example.techpulse.ui.presentation.settings.SettingsRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Zentrales Navigation-Graph-Host-Composable ([NavHost]) der TechPulse-Anwendung.
 *
 * Verwaltet die typsichere Navigation zwischen allen Hauptscreens ([Screen.Feed], [Screen.Repos],
 * [Screen.Bookmarks], [Screen.Settings]) sowie den Detailansichten ([Screen.RepoDetailRoute],
 * [Screen.PostDetailRoute]).
 *
 * @param navController Der [NavHostController] zur Steuerung des Navigationsflusses.
 * @param modifier Der [Modifier] zur externen Layout-Konfiguration.
 */
@Composable
fun TechPulseNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Feed,
        modifier = modifier
    ) {
        composable<Screen.Feed> {
            val feedViewModel: FeedViewModel = koinViewModel()
            FeedRoute(
                onPostClick = { postId ->
                    navController.navigate(Screen.PostDetailRoute(postId))
                },
                viewModel = feedViewModel
            )
        }

        composable<Screen.Repos> {
            val reposViewModel: ReposViewModel = koinViewModel()
            ReposRoute(
                viewModel = reposViewModel,
                onRepoClick = { repo ->
                    navController.navigate(Screen.RepoDetailRoute(repo.id))
                }
            )
        }

        composable<Screen.Bookmarks> {
            val bookmarksViewModel: BookmarksViewModel = koinViewModel()
            BookmarksRoute(
                viewModel = bookmarksViewModel,
                onPostClick = { bookmarkId ->
                    val repoId = bookmarkId.toLongOrNull()
                    if (repoId != null) {
                        navController.navigate(Screen.RepoDetailRoute(repoId = repoId))
                    } else {
                        navController.navigate(Screen.PostDetailRoute(postId = bookmarkId))
                    }
                }
            )
        }

        composable<Screen.Settings> {
            SettingsRoute(
                viewModel = koinViewModel(),
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.RepoDetailRoute> { backStackEntry ->
            val route: Screen.RepoDetailRoute = backStackEntry.toRoute()

            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<Screen.Repos>()
                } catch (_: Exception) {
                    null
                }
            }

            val reposViewModel: ReposViewModel = if (parentEntry != null) {
                koinViewModel(viewModelStoreOwner = parentEntry)
            } else {
                koinViewModel()
            }

            val repository: TechPulseRepository = koinInject()
            val reposUiState by reposViewModel.uiState.collectAsState()

            val repo = (reposUiState as? ReposUiState.Success)?.repos?.find { it.id == route.repoId }
                ?: reposViewModel.getRepoById(route.repoId)

            if (repo != null) {
                RepoDetailRoute(
                    repo = repo,
                    repository = repository,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable<Screen.PostDetailRoute> { backStackEntry ->
            val route: Screen.PostDetailRoute = backStackEntry.toRoute()

            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry<Screen.Feed>()
                } catch (_: Exception) {
                    null
                }
            }

            val feedViewModel: FeedViewModel = if (parentEntry != null) {
                koinViewModel(viewModelStoreOwner = parentEntry)
            } else {
                koinViewModel()
            }

            val postDetailViewModel: PostDetailViewModel = koinViewModel()
            val post = feedViewModel.getPostById(route.postId)

            if (post != null) {
                PostDetailRoute(
                    post = post,
                    onBookmarkClick = { clickedPost -> feedViewModel.toggleBookmark(clickedPost) },
                    onLikeClick = { postDetailViewModel.toggleLike(post.id, post) },
                    navController = navController,
                    viewModel = postDetailViewModel
                )
            }
        }
    }
}