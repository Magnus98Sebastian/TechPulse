package com.example.techpulse.ui.presentation.user

/**
 * Repräsentiert die verschiedenen UI-Zustände (UI State) des Benutzerprofils.
 *
 * Modelliert das Ladeverhalten ([Loading]), die Anzeige und Bearbeitung des Benutzernamens ([Success])
 * sowie Fehlerzustände ([Error]).
 */
sealed interface UserProfileUiState {

    /** Signalisiert, dass die Profil-Daten des Benutzers geladen werden. */
    data object Loading : UserProfileUiState

    /**
     * Signalisiert das erfolgreiche Laden oder Aktualisieren des Benutzerprofils.
     *
     * @property username Der aktuelle Name des Benutzers.
     * @property showNameDialog Steuert die Sichtbarkeit des Dialogs zur Namenseingabe.
     * @property errorMessage Eine optionale Fehlermeldung bei Validierungs- oder Speicherfehlern im Profil.
     */
    data class Success(
        val username: String = "",
        val showNameDialog: Boolean = false,
        val errorMessage: String? = null
    ) : UserProfileUiState

    /**
     * Signalisiert einen Fehler beim Laden oder Verarbeiten des Benutzerprofils.
     *
     * @property message Die Fehlermeldung zur Anzeige in der UI.
     */
    data class Error(
        val message: String
    ) : UserProfileUiState
}