package com.example.techpulse.ui.presentation.settings

/**
 * Repräsentiert die verschiedenen UI-Zustände (UI State) der Einstellungsansicht ([SettingsRoute]).
 *
 * Modelliert den Ladevorgang ([Loading]), die erfolgreiche Anzeige sowie Interaktion mit
 * den Nutzereinstellungen ([Success]) und das Auftreten von Fehlern ([Error]).
 */
sealed interface SettingsUiState {

    /** Signalisiert, dass die Benutzereinstellungen aktuell geladen werden. */
    data object Loading : SettingsUiState

    /**
     * Signalisiert das erfolgreiche Laden oder Bearbeiten der Benutzereinstellungen.
     *
     * @property username Der aktuell eingegebene oder gespeicherte Benutzername.
     * @property avatarUrl Die URI oder URL zum gespeicherten Profilbild des Benutzers.
     * @property isSavedSuccessfully Gibt an, ob die Einstellungen erfolgreich im Speicher/DataStore abgelegt wurden.
     * @property appVersion Die aktuelle Versionsnummer der Anwendung.
     */
    data class Success(
        val username: String = "",
        val avatarUrl: String = "",
        val isSavedSuccessfully: Boolean = false,
        val appVersion: String = "1.0.0"
    ) : SettingsUiState

    /**
     * Signalisiert einen Fehler beim Laden oder Speichern der Einstellungen.
     *
     * @property message Die Fehlermeldung zur Anzeige in der UI.
     */
    data class Error(
        val message: String
    ) : SettingsUiState
}
