package com.example.techpulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.techpulse.domain.Post
import com.example.techpulse.ui.components.CommentItem
import com.example.techpulse.ui.components.PostCard
import com.example.techpulse.ui.presentation.postDetail.PostDetailUiState
import androidx.compose.material.icons.automirrored.filled.ArrowBack

/**
 * Screen-Composable zur Anzeige der Detailansicht eines einzelnen Beitrags samt Kommentaren.
 *
 * Stellt den Beitrag über eine [PostCard] dar und bietet unterhalb eine Liste von Kommentaren ([CommentItem]),
 * gesteuert durch den [uiState]. Enthält außerdem eine Eingabeleiste am unteren Bildschirmrand zum Absenden
 * neuer Kommentare.
 *
 * @param post Der als Ausgangsbasis übergebene Beitrag ([Post]).
 * @param uiState Der aktuelle UI-Zustand der Detailansicht ([PostDetailUiState]), der Kommentare und ggf. aktualisierte Beitragsdaten liefert.
 * @param onBackClick Callback für den Zurück-Button in der TopAppBar.
 * @param onBookmarkClick Callback zum Umschalten des Lesezeichen-Status für den aktuellen Beitrag.
 * @param onLikeClick Callback zum Auslösen einer Like-Aktion für den aktuellen Beitrag.
 * @param onSendComment Callback zum Absenden eines neu eingegebenen Kommentars mit dem jeweiligen Text.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: Post,
    uiState: PostDetailUiState,
    onBackClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSendComment: (String) -> Unit = {}
) {
    var commentText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentPost = if (uiState is PostDetailUiState.Success && uiState.post != null) {
        uiState.post
    } else {
        post
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Detail", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding(),
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Kommentar schreiben...") },
                        modifier = Modifier
                            .weight(1f),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onSendComment(commentText)
                                commentText = ""
                                keyboardController?.hide()
                            }
                        },
                        enabled = commentText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Senden"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PostCard(
                    post = currentPost,
                    onLikeClick = onLikeClick,
                    onCommentCLick = { },
                    onBookmarkClick = onBookmarkClick,
                    onPostClick = { },
                    isExpandableText = true
                )
            }
            when (uiState) {
                is PostDetailUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is PostDetailUiState.Success -> {
                    if (uiState.comments.isEmpty()) {
                        item {
                            Text(
                                text = "Noch keine Kommentare vorhanden.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(
                            uiState.comments,
                            key = { it.id }
                        ) { comment ->
                            CommentItem(comment = comment)
                        }
                    }
                }
                is PostDetailUiState.Error -> {
                    item {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
