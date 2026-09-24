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
 * Screen-Composable zur Anzeige des Haupt-Feeds mit Beiträgen.
 *
 * Verwaltet die Zustände [FeedUiState.Loading], [FeedUiState.Error] und [FeedUiState.Success].
 * Stellt im Erfolgsfall eine scrollbare Liste von Beiträgen ([PostCard]) bereit oder zeigt
 * entsprechende Meldungen bei leeren Beiträgen bzw. Fehlern an.
 *
 * @param uiState Der aktuelle UI-Zustand des Haupt-Feeds ([FeedUiState]).
 * @param onPostClick Callback zur Navigation zur Detailansicht eines Beitrags mittels dessen ID.
 * @param onBookmarkClick Callback zum Umschalten des Lesezeichen-Status für einen Beitrag ([Post]).
 * @param onLikeClick Callback zum Auslösen einer Like-Aktion unter Angabe von Beitrags-ID und [Post]-Objekt.
 * @param onRefresh Callback zum erneuten Laden der Daten im Fehlerfall.
 * @param modifier Der [Modifier] zur Anpassung des Screen-Layouts.
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
                        Text(text = uiState.message)
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
                                    onCommentCLick = { onPostClick(post.id) },
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