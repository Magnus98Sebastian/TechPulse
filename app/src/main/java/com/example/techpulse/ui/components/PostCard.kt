package com.example.techpulse.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.techpulse.domain.Post
import com.example.techpulse.domain.util.ContentParser
import com.example.techpulse.ui.theme.TechPulseGradient

/**
 * Eine Jetpack Compose UI-Komponente zur Darstellung eines einzelnen Beitrags ([Post]) in einer Card-Ansicht.
 *
 * @param post Das [Post]-Domänenmodell mit allen Daten des Beitrags.
 * @param onLikeClick Callback beim Klick auf den Like-Button.
 * @param onCommentClick Callback beim Klick auf den Kommentar-Button.
 * @param onBookmarkClick Callback beim Klick auf den Lesezeichen-Button.
 * @param onPostClick Callback beim Klick auf die gesamte Card oder das Menü-Icon.
 * @param modifier Der [Modifier] zur externen Layout-Konfiguration.
 * @param isExpandableText Bestimmt, ob der Fließtext ein-/ausklappbar dargestellt werden soll.
 */
@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpandableText: Boolean = false
) {
    val parsedContent = remember(post.contentText) {
        ContentParser.parseContentText(post.contentText)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onPostClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: User Info & Menü
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.userAvatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profilbild von ${post.username}",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = TechPulseGradient,
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.timestamp,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onPostClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Weitere Optionen"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Titel
            if (parsedContent.title.isNotBlank()) {
                Text(
                    text = parsedContent.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Fließtext
            if (parsedContent.cleanBody.isNotEmpty()) {
                if (isExpandableText) {
                    ExpandableText(
                        text = parsedContent.cleanBody,
                        collapsedMaxLines = 4
                    )
                } else {
                    Text(
                        text = parsedContent.cleanBody,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Beitragsbild
            if (!post.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Beitragsbild",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Aktionsleiste (Like, Comment, Bookmark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val heartColor by animateColorAsState(
                        targetValue = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "heartColor"
                    )
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (post.isLiked) "Gefällt mir nicht mehr" else "Gefällt mir",
                            tint = heartColor
                        )
                    }
                    Text(
                        text = post.likeCount.toString(),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onCommentClick) {
                        Icon(
                            imageVector = Icons.Outlined.ModeComment,
                            contentDescription = "Kommentare anzeigen",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = post.commentCount.toString(),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        imageVector = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (post.isBookmarked) "Lesezeichen entfernen" else "Lesezeichen hinzufügen",
                        tint = if (post.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}