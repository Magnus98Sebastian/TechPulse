package com.example.techpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.techpulse.data.local.BookmarkDocument
import com.example.techpulse.data.mapper.toPost
import com.example.techpulse.domain.Post
import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.ui.components.PostCard
import com.example.techpulse.ui.components.RepoItemCard
import com.example.techpulse.ui.presentation.bookmarks.BookmarksUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    uiState: BookmarksUiState,
    onBookmarkToggle: (BookmarkDocument) -> Unit,
    onPostClick: (String) -> Unit,
    onRepoClick: (Long) -> Unit,
    onLikeClick: (String, Post) -> Unit,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Posts", "Repositories")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lesezeichen") }) }
    ) { innerPadding ->
        when (uiState) {
            is BookmarksUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BookmarksUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.message)
                }
            }
            is BookmarksUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                ) {
                    PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    val filteredBookmarks = remember(uiState.bookmarks, selectedTabIndex) {
                        if (selectedTabIndex == 0) {
                            uiState.bookmarks.filter { it.type == "POST" ||(it.type != "REPOSITORY" && it.type.isNotBlank()) }
                        } else {
                            uiState.bookmarks.filter { it.type == "REPOSITORY" }
                        }
                    }

                    if (filteredBookmarks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedTabIndex == 0) "Keine gespeicherten Posts." else "Keine gespeicherten Repositories.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top = 12.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + 80.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (selectedTabIndex == 0) {
                                items(
                                    items = filteredBookmarks,
                                    key = { it.id }
                                ) { bookmark ->
                                    val post = bookmark.toPost()

                                    PostCard(
                                        post = post,
                                        onLikeClick = { onLikeClick(post.id, post) },
                                        onCommentCLick = { onPostClick(post.id) },
                                        onBookmarkClick = { onBookmarkToggle(bookmark) },
                                        onPostClick = { onPostClick(post.id) },
                                        isExpandableText = false,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else {
                                items(
                                    items = filteredBookmarks,
                                    key = { it.id }
                                ) { bookmark ->
                                    val repoItem = remember(bookmark) {
                                        RepositoryItem(
                                            id = bookmark.id.toLongOrNull() ?: 0L,
                                            name = bookmark.title,
                                            ownerName = bookmark.sourceName,
                                            ownerAvatarUrl = bookmark.imageUrl.ifEmpty { bookmark.userAvatarUrl },
                                            description = bookmark.description,
                                            language = null,
                                            starsCount = bookmark.likeCount,
                                            forksCount = 0,
                                            openIssuesCount = 0,
                                            watchersCount = 0,
                                            defaultBranch = "main",
                                            licenseName = null,
                                            topics = emptyList(),
                                            htmlUrl = null
                                        )
                                    }

                                    RepoItemCard(
                                        repo = repoItem,
                                        onRepoClick = { repo -> onRepoClick(repo.id) },
                                        modifier = Modifier.fillMaxWidth()
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