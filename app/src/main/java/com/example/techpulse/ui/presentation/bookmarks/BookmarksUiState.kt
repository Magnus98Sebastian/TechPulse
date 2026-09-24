package com.example.techpulse.ui.presentation.bookmarks

import com.example.techpulse.data.local.BookmarkDocument

/**
 * Repräsentiert die verschiedenen Zustände (UI State) der Lesezeichenansicht ([BookmarksRoute]).
 *
 * Folgt dem Lade- / Erfolgs- / Fehler-Muster zur sauberen Zustandstrennung in der UI.
 */
sealed interface BookmarksUiState {

    /** Signalisiert, dass die gespeicherten Lesezeichen geladen werden. */
    data object Loading : BookmarksUiState

    /**
     * Signalisiert das erfolgreiche Laden der Lesezeichen.
     *
     * @property bookmarks Die Liste aller gespeicherten [BookmarkDocument]-Einträge.
     */
    data class Success(
        val bookmarks: List<BookmarkDocument>
    ) : BookmarksUiState

    /**
     * Signalisiert einen Fehler beim Laden oder Verarbeiten der Lesezeichen.
     *
     * @property message Die Fehlermeldung zur Anzeige in der UI.
     */
    data class Error(
        val message: String
    ) : BookmarksUiState
}