package com.example.techpulse.ui.presentation.feed

import com.example.techpulse.domain.Post

/**
 * Repräsentiert die verschiedenen Zustände (UI State) der Feed-Ansicht ([FeedRoute]).
 *
 * Folgt dem Lade- / Erfolgs- / Fehler-Muster zur sauberen Zustandstrennung in der UI.
 */
sealed interface FeedUiState {

    /** Signalisiert, dass die Feed-Beiträge geladen werden. */
    data object Loading : FeedUiState

    /**
     * Signalisiert das erfolgreiche Laden der Feed-Beiträge.
     *
     * @property posts Die Liste der geladenen [Post]-Beiträge.
     */
    data class Success(
        val posts: List<Post> = emptyList(),
        val isLoadingMore: Boolean = false
        ) : FeedUiState

    /**
     * Signalisiert einen Fehler beim Laden oder Aktualisieren des Feeds.
     *
     * @property message Die Fehlermeldung zur Anzeige in der UI.
     */
    data class Error(val message: String) : FeedUiState
}
