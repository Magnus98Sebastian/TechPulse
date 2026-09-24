package com.example.techpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.ui.components.RepoItemCard
import com.example.techpulse.ui.presentation.repos.ReposUiState

/**
 * Screen-Composable zur Anzeige und Durchsuchung einer Liste von GitHub-Repositorys.
 *
 * Beinhaltet ein Suchfeld zur Eingabe von Filtern sowie eine Zustandshandhabung für [ReposUiState.Idle],
 * [ReposUiState.Loading], [ReposUiState.Error] und [ReposUiState.Success].
 * Unterstützt unendliches Scrollen (Paging) durch automatisches Auslösen von [onLoadNextPage],
 * sobald das Ende der Liste erreicht wird.
 *
 * @param searchQuery Der aktuell im Suchfeld eingegebene Text.
 * @param uiState Der aktuelle UI-Zustand der Repository-Übersicht ([ReposUiState]).
 * @param onSearchQueryChanged Callback, wenn sich die Eingabe im Suchfeld ändert.
 * @param onRepoClick Callback beim Auswählen eines Repositorys ([RepositoryItem]).
 * @param onLoadNextPage Callback zum Nachladen weiterer Ergebnisse beim Scrollen an das Listenende.
 * @param modifier Der [Modifier] zur Anpassung des Screen-Layouts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReposScreen(
    searchQuery: String,
    uiState: ReposUiState,
    onSearchQueryChanged: (String) -> Unit,
    onRepoClick: (RepositoryItem) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 5 && totalItems > 0
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Repositories") }) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Filtere Repositories...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    ReposUiState.Idle -> {
                        Text("Suche nach Repositories...")
                    }
                    ReposUiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is ReposUiState.Error -> {
                        Text("Fehler: ${uiState.message}")
                    }
                    is ReposUiState.Success -> {
                        if (uiState.repos.isEmpty()) {
                            Text("Keine Repositories gefunden")
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = innerPadding.calculateBottomPadding() + 80.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = uiState.repos,
                                    key = { it.id }
                                ) { repo ->
                                    RepoItemCard(
                                        repo = repo,
                                        onRepoClick =  onRepoClick
                                    )
                                }
                            }

                            LaunchedEffect(shouldLoadMore.value) {
                                if (shouldLoadMore.value) {
                                    onLoadNextPage()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}