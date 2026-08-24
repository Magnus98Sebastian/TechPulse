package com.example.techpulse.ui.presentation.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.repository.TechPulseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ReposViewModel : ViewModel() {
    private val repository = TechPulseRepository()
    private val _uiState = MutableStateFlow<ReposUiState>(ReposUiState.Idle)
    val uiState: StateFlow<ReposUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("Kotlin")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        _searchQuery
            .debounce(500L)
            .distinctUntilChanged()
            .filter {it.isNotBlank()}
            .onEach { query ->
                executeSearch(query)
            }
            .launchIn(viewModelScope)

    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isBlank()) {
            _uiState.value = ReposUiState.Idle
        }
    }

    private suspend fun executeSearch(query: String) {
        _uiState.value = ReposUiState.Loading
        repository.searchRepositories(query)
            .onSuccess { repos ->
                _uiState.value = if (repos.isEmpty()) {
                    ReposUiState.Error("Keine Ergebnisse gefunden")
                } else {
                    ReposUiState.Success(repos)
                }
            }
            .onFailure { error ->
                _uiState.value = ReposUiState.Error(error.localizedMessage ?: "Fehler beim Laden")
            }
    }
}