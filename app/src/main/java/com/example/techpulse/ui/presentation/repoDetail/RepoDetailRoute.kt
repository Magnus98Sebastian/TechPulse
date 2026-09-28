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

/**
 * Route-Composable für die Repository-Detailansicht.
 *
 * Verwaltet den Lesezeichen-Status des ausgewählten [RepositoryItem]s über das [TechPulseRepository],
 * steuert das Umschalten von Lesezeichen in einem Coroutine-Scope und bindet die UI der [RepoDetailScreen] an.
 *
 * @param repo Das anzuzeigende [RepositoryItem]-Objekt mit den Detaildaten.
 * @param repository Das Repository zur Abfrage und Verwaltung von Lesezeichen.
 * @param onBackClick Callback für die Aktion beim Klick auf die Zurück-Schaltfläche.
 * @param modifier Der auf das Composable anzuwendende [Modifier].
 */
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