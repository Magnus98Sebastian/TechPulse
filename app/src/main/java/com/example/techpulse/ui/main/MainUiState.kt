package com.example.techpulse.ui.main

/**
 * Repräsentiert den UI-Zustand ([MainUiState]) der Hauptnavigations-Ansicht.
 *
 * Kapselt Informationen über Ladezustände, die Dialog-Steuerung
 * sowie etwaige Fehlermeldungen bei der Benutzerinitialisierung.
 *
 * @property isLoading Gibt an, ob initiale Daten oder Benutzerinformationen geladen werden.
 * @property showNameDialog Steuert die Sichtbarkeit des Dialogs zur Benutzernamenseingabe.
 * @property errorMessage Enthält eine optionale Fehlermeldung (z. B. bei Validierungsfehlern), sonst `null`.
 * @property isUsernameSaving Gibt an, ob das Speichern des Benutzernamens aktuell verarbeitet wird.
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val showNameDialog: Boolean = false,
    val errorMessage: String? = null,
    val isUsernameSaving: Boolean = false
)
