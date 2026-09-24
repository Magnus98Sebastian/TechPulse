package com.example.techpulse.ui.presentation.repoDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.ui.screens.RepoDetailScreen
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@Composable
fun RepoDetailRoute(
    repo: RepositoryItem,
    repository: TechPulseRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val bookmarks by repository.bookmarks.collectAsState(initial = emptyList())

    val isBookmarked = bookmarks.any { it.id == repo.id.toString() }

    RepoDetailScreen(
        repo = repo,
        isBookmarked = isBookmarked,
        onBookmarkClick = {
            coroutineScope.launch {
                repository.toggleRepoBookmark(repo)
            }
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}