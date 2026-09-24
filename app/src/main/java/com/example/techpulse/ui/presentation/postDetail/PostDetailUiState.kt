package com.example.techpulse.ui.presentation.postDetail

import com.example.techpulse.domain.Comment
import com.example.techpulse.domain.Post

/**
 * Repräsentiert die verschiedenen UI-Zustände (UI State) der Detailansicht eines Beitrags ([PostDetailRoute]).
 *
 * Kapselt das Ladeverhalten sowie die erfolgreiche Bereitstellung von Beitrags-Details und dazugehörigen Kommentaren.
 */
sealed interface PostDetailUiState {

    /** Signalisiert, dass die Beitrags-Details und Kommentare geladen werden. */
    data object Loading : PostDetailUiState

    /**
     * Signalisiert das erfolgreiche Laden der Beitrags-Details und/oder der Kommentare.
     *
     * @property post Das aktualisierte [Post]-Objekt oder `null`, falls nur Kommentare geladen wurden.
     * @property comments Die Liste der zum Beitrag gehörigen [Comment]-Objekte.
     * @property errorMessage Eine optionale Fehlermeldung (z. B. bei partiellen Fehlern beim Senden eines Kommentars).
     */
    data class Success(
        val post: Post? = null,
        val comments: List<Comment>,
        val errorMessage: String? = null
    ) : PostDetailUiState

    /**
     * Signalisiert einen kritischen Fehler beim Laden der Detailansicht.
     *
     * @property message Die Fehlermeldung zur Anzeige in der UI.
     */
    data class Error(val message: String) : PostDetailUiState
}