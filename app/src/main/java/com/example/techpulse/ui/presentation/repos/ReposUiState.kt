package com.example.techpulse.ui.presentation.repos

import com.example.techpulse.domain.RepositoryItem

/**
 * Repräsentiert die verschiedenen UI-Zustände (UI State) der Repository-Suche ([ReposRoute]).
 *
 * Modelliert den kompletten Lebenszyklus von der initialen Inaktivität ([Idle]) über Ladevorgänge ([Loading])
 * bis hin zur erfolgreichen Datenanzeige ([Success]) oder Fehlerbehandlung ([Error]).
 */
sealed interface ReposUiState {

    /** Initialer Zustand vor Ausführung einer ersten Suchanfrage. */
    data object Idle : ReposUiState

    /** Signalisiert, dass Repository-Daten über das Netzwerk geladen oder gefiltert werden. */
    data object Loading : ReposUiState

    /**
     * Signalisiert das erfolgreiche Laden der Repository-Ergebnisse.
     *
     * @property repos Die Liste der gefundenen [RepositoryItem]-Objekte.
     */
    data class Success(val repos: List<RepositoryItem>) : ReposUiState

    /**
     * Signalisiert einen Fehler beim Abrufen der Repositories.
     *
     * @property message Die Fehlermeldung zur Anzeige in der UI.
     */
    data class Error(val message: String) : ReposUiState
}