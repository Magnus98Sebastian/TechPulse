package com.example.techpulse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.techpulse.ui.presentation.bookmarks.BookmarksViewModel
import com.example.techpulse.ui.presentation.feed.FeedViewModel
import com.example.techpulse.ui.presentation.repos.ReposViewModel

@Composable
fun TechPulseNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Repos,
        modifier = modifier
    ) {
        composable<Screen.Feed>{
            val feedViewModel: FeedViewModel = viewModel()
            FeedRoute(viewModel = feedViewModel)
        }
        composable<Screen.Repos>{
            val reposViewModel: ReposViewModel = viewModel()
            ReposRoute(viewModel = reposViewModel)
        }
        composable<Screen.Bookmarks>{
            val bookmarksViewModel: BookmarksViewModel = viewModel()
            BookmarksRoute(viewModel = bookmarksViewModel)
        }
    }
}