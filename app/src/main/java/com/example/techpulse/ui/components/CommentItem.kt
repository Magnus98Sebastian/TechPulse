package com.example.techpulse.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.techpulse.domain.Comment

/**
 * Repräsentiert eine Compose-UI-Komponente zur Darstellung eines einzelnen [Comment] in einer Card-Ansicht.
 *
 * Rendert das Profilbild (Avatar) des Verfassers mit automatischem Fallback-Bild, den Benutzernamen
 * sowie den eigentlichen Fließtext des Kommentars.
 *
 * @param comment Das [Comment]-Domain-Modell, das die darzustellenden Daten enthält.
 * @param modifier Der optional anwendbare [Modifier] für Layout-Anpassungen von außen.
 */
@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(comment.userAvatarUrl.takeIf { !it.isNullOrBlank() }
                        ?: "https://dev.to/assets/sparkle-heart-5f9bee37d7a37719658692e19277d1209b02b5e634b3011340a5015b3e648873.png") // Fallback-Bild
                    .crossfade(true)
                    .build(),
                contentDescription = "Profilbild von ${comment.username}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = comment.username,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.contentText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}