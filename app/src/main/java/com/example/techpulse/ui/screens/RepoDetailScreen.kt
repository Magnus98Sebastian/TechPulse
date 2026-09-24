package com.example.techpulse.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.domain.util.getLanguageColor
import com.example.techpulse.domain.util.toKFormattedString
import com.example.techpulse.ui.components.StatCard

/**
 * Screen-Composable zur Anzeige der Detailansicht eines GitHub-Repositorys.
 *
 * Stellt detaillierte Informationen wie Repository-Eigentümer, Sprache, Kennzahlen
 * (Stars, Forks, Issues, Watchers, Branch, Lizenz), Beschreibung sowie Topics dar.
 * Bietet zusätzlich Aktions-Buttons zum Öffnen der README-Datei oder der GitHub-Seite im Browser.
 *
 * @param repo Das anzuzeigende Repository-Objekt ([RepositoryItem]).
 * @param isBookmarked Gibt an, ob das Repository aktuell als Lesezeichen gespeichert ist.
 * @param onBookmarkClick Callback zum Umschalten des Lesezeichen-Status für dieses Repository.
 * @param onBackClick Callback für den Zurück-Button in der TopAppBar.
 * @param modifier Der [Modifier] zur Anpassung des Screen-Layouts.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RepoDetailScreen(
    repo: RepositoryItem,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repo.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onBookmarkClick) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Lesezeichen entfernen" else "Lesezeichen hinzufügen",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = repo.ownerName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = repo.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val languageColor = getLanguageColor(repo.language)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(languageColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = repo.language ?: "Unbekannte Sprache",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Star,
                    value = repo.starsCount.toKFormattedString(),
                    label = "Stars",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Share,
                    value = repo.forksCount.toKFormattedString(),
                    label = "Forks",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Info,
                    value = repo.openIssuesCount.toString(),
                    label = "Issues",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Visibility,
                    value = repo.watchersCount.toKFormattedString(),
                    label = "Watchers",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Code,
                    value = repo.defaultBranch ?: "main",
                    label = "Branch",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.Description,
                    value = repo.licenseName ?: "Keine",
                    label = "Lizenz",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Beschreibung",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = repo.description ?: "Keine Beschreibung vorhanden.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (repo.topics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Topics",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repo.topics.forEach { topic ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(topic) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            repo.readmeUrl?.let { url ->
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("README im Browser lesen")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            repo.htmlUrl?.let { url ->
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Repository auf GitHub öffnen")
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}