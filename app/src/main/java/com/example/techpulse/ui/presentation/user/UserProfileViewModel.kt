package com.example.techpulse.ui.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.repository.TechPulseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel zur Verwaltung des Benutzerprofils und der Initialisierung von Benutzernamen.
 *
 * Steuert das Laden, Validieren und Speichern von Benutzernamen über das [TechPulseRepository],
 * inklusive Prüfung auf Eindeutigkeit und automatischer Erzeugung von Zufallsnamen bei leerer Eingabe.
 *
 * @property repository Das Repository für den Zugriff auf lokal gespeicherte Benutzerdaten und Namensprüfungen.
 */
class UserProfileViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)

    /** Der reaktive [StateFlow] des aktuellen [UserProfileUiState]. */
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    /**
     * Lädt den aktuell gespeicherten Benutzernamen aus dem [repository] und setzt den Zustand auf [UserProfileUiState.Success].
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            val name = repository.getUsername() ?: ""
            _uiState.value = UserProfileUiState.Success(username = name)
        }
    }
}
