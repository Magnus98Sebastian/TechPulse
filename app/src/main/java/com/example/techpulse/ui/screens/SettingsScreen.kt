package com.example.techpulse.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.techpulse.ui.presentation.settings.SettingsUiState
import com.example.techpulse.ui.theme.TechPulseGradient

/**
 * Screen-Composable zur Verwaltung der App-Einstellungen und des Benutzerprofils.
 *
 * Bietet Eingabemöglichkeiten zum Ändern des Benutzernamens, zum Auswählen oder Entfernen
 * eines Profilbilds (Avatar) aus der Bildergallerie sowie zum Speichern der Änderungen.
 * Zeigt außerdem Versions- und App-Informationen an und handhabt Lade- ([SettingsUiState.Loading])
 * sowie Fehlerzustände ([SettingsUiState.Error]).
 *
 * @param uiState Der aktuelle UI-Zustand der Einstellungen ([SettingsUiState]).
 * @param onBackClick Callback für den Zurück-Button in der TopAppBar.
 * @param onUsernameChange Callback, wenn der Benutzername im Eingabefeld geändert wird.
 * @param onAvatarSelected Callback, wenn ein neues Profilbild über den Photos-Picker ausgewählt wurde.
 * @param onRemoveAvatar Callback zum Entfernen des aktuellen Profilbilds.
 * @param onSaveClick Callback zum Speichern der geänderten Einstellungen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onAvatarSelected: (Context, Uri) -> Unit,
    onRemoveAvatar: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onAvatarSelected(context, uri)
            }
        }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is SettingsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SettingsUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is SettingsUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Benutzerkonto",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Benutzername",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Wird verwendet, um deine persönlichen Feeds und Repositories zu laden.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = uiState.username,
                                    onValueChange = onUsernameChange,
                                    label = { Text("Benutzername") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    },
                                    trailingIcon = {
                                        if (uiState.isSavedSuccessfully) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Gespeichert",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if(uiState.avatarUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = uiState.avatarUrl,
                                            contentDescription = "Profilbild Vorschau",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(88.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 2.dp,
                                                    brush = TechPulseGradient,
                                                    shape = CircleShape
                                                )
                                        )
                                    } else {
                                        Surface(
                                            modifier = Modifier
                                                .size(88.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 2.dp,
                                                    brush = TechPulseGradient,
                                                    shape = CircleShape
                                                ),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = "Standard Profilbild",
                                                    modifier = Modifier.size(48.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

//                                    OutlinedButton(
//                                        onClick = {
//                                            photoPickerLauncher.launch(
//                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                                            )
//                                        },
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        Icon(Icons.Default.Image, contentDescription = null)
//                                        Spacer(modifier = Modifier.width(8.dp))
//                                        Text("Bild aus Galerie wählen")
//                                    }

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            }
                                        ) {
                                            Icon(Icons.Default.Image, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if(uiState.avatarUrl.isNotBlank()){
                                                "Bild ändern"
                                            } else {
                                                "Bild aus Galerie wählen"
                                            })
                                        }
                                        if(uiState.avatarUrl.isNotBlank()) {
                                            OutlinedButton(
                                                onClick = onRemoveAvatar
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Bild entfernen",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Bild entfernen", color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = onSaveClick,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = uiState.username.isNotBlank()
                                ) {
                                    Text("Speichern")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Über die App",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(text = "TechPulse App", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "Version ${uiState.appVersion}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}