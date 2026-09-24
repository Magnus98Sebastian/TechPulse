package com.example.techpulse.ui.presentation.repos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.ui.screens.ReposScreen

/**
 * Route-Composable für die GitHub-Repositories-Ansicht.
 *
 * Verbindet das [ReposViewModel] mit der zustandslosen [ReposScreen]-UI.
 * Beobachtet den aktuellen [ReposUiState] sowie die Suchanfrage ([searchQuery])
 * und leitet UI-Events wie Suchfeldeingaben, Repositoriumsauswahl und Paginierung weiter.
 *
 * @param viewModel Das injected [ReposViewModel] zur Verwaltung der Repository-Daten und des Suchzustands.
 * @param onRepoClick Callback bei Auswahl eines Repositoriums ([RepositoryItem]), z. B. zur Detail- oder Webanzeige.
 * @param modifier Der [Modifier] zur externen Layout-Konfiguration.
 */
@Composable
fun ReposRoute(
    viewModel: ReposViewModel,
    onRepoClick: (RepositoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    ReposScreen(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChange,
        onRepoClick = onRepoClick,
        onLoadNextPage = { viewModel.loadNextPage() },
        modifier = modifier
    )
}