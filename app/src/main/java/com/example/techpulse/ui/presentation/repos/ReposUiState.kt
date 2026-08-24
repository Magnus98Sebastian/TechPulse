package com.example.techpulse.ui.presentation.repos

import com.example.techpulse.domain.RepositoryItem

sealed interface ReposUiState {
    data object  Idle : ReposUiState
    data object Loading : ReposUiState
    data class Success(val repos: List<RepositoryItem>) : ReposUiState
    data class Error(val message: String) : ReposUiState
}