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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.example.techpulse.domain.Post
import com.example.techpulse.ui.components.PostCard
import com.example.techpulse.ui.presentation.feed.FeedUiState

/**
 * Haupt-Screen zur Anzeige des Beitrags-Feeds ([FeedUiState]).
 *
 * Stellt Beitragslisten dar, unterstützt automatisches Nachladen von weiteren Inhalten (Pagination/Infinite Scrolling)
 * beim Erreichen des Listenendes sowie Lade-, Fehler- und Leerzustände.
 *
 * @param uiState Der aktuelle UI-Zustand des Feeds ([FeedUiState]).
 * @param onLoadMore Callback zum Anfordern weiterer Beiträge beim Erreichen des Scroll-Endes.
 * @param onPostClick Callback beim Klick auf einen Beitrag oder den Kommentar-Button, übergibt die Post-ID.
 * @param onBookmarkClick Callback zum Speichern oder Entfernen eines Beitrags aus den Lesezeichen.
 * @param onLikeClick Callback beim Klick auf den Like-Button eines Beitrags, übergibt die Post-ID und den [Post].
 * @param onRefresh Callback zum erneuten Laden des Feeds im Fehlerfall.
 * @param modifier Der [Modifier] zur Anpassung des Layouts dieser Composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onLoadMore: () -> Unit,
    onPostClick: (String) -> Unit,
    onBookmarkClick: (Post) -> Unit,
    onLikeClick: (String, Post) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    val shouldeLoadMore = remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibilityItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItemsCount > 0 && lastVisibilityItemIndex >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldeLoadMore.value) {
        if (shouldeLoadMore.value) {
            onLoadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Feed", style = MaterialTheme.typography.titleLarge) })
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is FeedUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is FeedUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Fehler: Bitte überprüfen sie Ihre Verbindung.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRefresh) { Text("Erneut versuchen") }
                    }
                }
                is FeedUiState.Success -> {
                    if (uiState.posts.isEmpty()) {
                        Text(
                            text = "Keine Beiträge gefunden",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(top = 8.dp,
                                bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.posts,
                                key = { it.id }
                            ) { post ->
                                PostCard(
                                    post = post,
                                    onLikeClick = { onLikeClick(post.id, post) },
                                    onCommentClick = { onPostClick(post.id) },
                                    onBookmarkClick = { onBookmarkClick(post) },
                                    onPostClick = { onPostClick(post.id) },
                                    modifier = Modifier.fillMaxWidth(),
                                    isExpandableText = false
                                )
                            }
                            if (uiState.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp),
                                            strokeWidth = 3.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}