package com.example.techpulse.ui.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techpulse.ui.screens.SettingsScreen

/**
 * Route-Composable für die Einstellungen-Ansicht ([SettingsScreen]).
 *
 * Verbindet das [SettingsViewModel] mit dem zustandslosen [SettingsScreen].
 * Beobachtet den [SettingsUiState] lebenszyklusbewusst mittels [collectAsStateWithLifecycle]
 * und leitet Benutzerinteraktionen wie Benutzernamensänderungen, Profilbildauswahl,
 * das Entfernen des Profilbilds und das Speichern der Einstellungen an das ViewModel weiter.
 *
 * @param viewModel Das injected [SettingsViewModel] zur Verwaltung des Einstellungszustands.
 * @param onBackClick Callback für die Navigation zurück zum vorherigen Screen.
 */
@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onUsernameChange = { newUsername ->
            viewModel.onUsernameChange(newUsername)
        },
        onAvatarSelected = {context, uri ->
            viewModel.onAvatarSelected(context, uri)
        },
        onRemoveAvatar = {
            viewModel.removeAvatar()
        },
        onSaveClick = {
            viewModel.saveSettings()
        }
    )
}