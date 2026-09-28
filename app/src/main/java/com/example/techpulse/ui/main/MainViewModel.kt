package com.example.techpulse.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.repository.TechPulseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Repräsentiert das ViewModel für die Hauptansicht und die globale Initialisierung der Anwendung.
 *
 * Verwaltet den Anwendungszustand ([MainUiState]) beim App-Start, stellt die anonyme
 * Firebase-Authentifizierung sicher, prüft den Onboarding-Status des Benutzers und
 * handhabt die Validierung sowie das Speichern des Benutzernamens.
 *
 * @property repository Das [TechPulseRepository] für Datenzugriffe und Firebase-Operationen.
 */
class MainViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())

    /** Der beobachtbare [StateFlow] des aktuellen UI-Zustands. */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkUserSetup()
    }

    /**
     * Prüft beim Start der Anwendung den Authentifizierungs- und Einrichtungsstatus des Benutzers.
     *
     * Stellt sicher, dass ein anonymer Firebase-Account existiert, und blendet den
     * Namenseingabe-Dialog ein, falls das Setup noch nicht abgeschlossen wurde.
     */
    private fun checkUserSetup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.ensureAnonymousUser()

                val isSetupCompleted = repository.isUserSetupCompleted()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showNameDialog = !isSetupCompleted
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Fehler beim Initialisieren: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Verarbeitet die Eingabe eines Benutzernamens aus dem Dialog.
     *
     * Prüft bei eingegebenem Namen, ob dieser bereits von einem anderen Benutzer verwendet wird.
     * Ist die Eingabe leer, erzeugt das Repository automatisch einen zufälligen Namen.
     *
     * @param inputName Der vom Benutzer eingegebene Name.
     */
    fun onConfirmUsername(inputName: String) {
        viewModelScope.launch {
            val trimmedName = inputName.trim()

            if (trimmedName.isNotBlank()) {
                _uiState.update { it.copy(isUsernameSaving = true, errorMessage = null) }
                val isTaken = repository.isUsernameTaken(trimmedName)

                if (isTaken) {
                    _uiState.update {
                        it.copy(
                            isUsernameSaving = false,
                            errorMessage = "Dieser Benutzername ist bereits vergeben."
                        )
                    }
                    return@launch
                }
            } else {
                _uiState.update { it.copy(isUsernameSaving = true, errorMessage = null) }
            }

            try {
                repository.saveUsername(trimmedName)
                _uiState.update {
                    it.copy(
                        showNameDialog = false,
                        isUsernameSaving = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUsernameSaving = false,
                        errorMessage = "Fehler beim Speichern des Benutzernamens: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Wird aufgerufen, wenn der Benutzer den Namensdialog abbricht.
     * Triggert die automatische Vergabe eines Zufallsnamens durch Übergabe eines leeren Strings.
     */
    fun onDismissDialog() {
        onConfirmUsername("")
    }
}