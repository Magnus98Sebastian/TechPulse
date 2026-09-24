package com.example.techpulse.ui.presentation.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.domain.RepositoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel zur Verwaltung der GitHub-Repository-Suche und -Anzeige ([ReposRoute]).
 *
 * Verwaltet das Laden, Filtern und Paginieren von Repository-Einträgen ([RepositoryItem])
 * aus dem [TechPulseRepository] und berechnet reaktiv den passenden [ReposUiState].
 *
 * @property repository Das Repository für Suchanfragen und Datenzugriffe auf GitHub-Repositories.
 */
class ReposViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    /** Der reaktive [StateFlow] der aktuellen Suchanfrage. */
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allRepos = MutableStateFlow<List<RepositoryItem>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private var currentPage = 1
    private var isNextPageLoading = false

    /**
     * Der reaktive [StateFlow] des aktuellen [ReposUiState].
     *
     * Kombiniert den geladenen Gesamtbestand, die aktuelle Suchanfrage sowie Lade- und Fehlerzustände.
     * Filtert die Repositories nach Name, Besitzer, Beschreibung und Programmiersprache.
     */
    val uiState: StateFlow<ReposUiState> = combine(
        _allRepos,
        _searchQuery,
        _isLoading,
        _errorMessage
    ) { repos, query, isLoading, error ->
        when {
            isLoading -> ReposUiState.Loading
            error != null -> ReposUiState.Error(error)
            else -> {
                val filtered = if (query.isBlank()) {
                    repos
                } else {
                    repos.filter { repo ->
                        repo.name.contains(query, ignoreCase = true) ||
                                repo.ownerName.contains(query, ignoreCase = true) ||
                                (repo.description?.contains(query, ignoreCase = true) ?: false) ||
                                (repo.language?.contains(query, ignoreCase = true) ?: false)
                    }
                }
                ReposUiState.Success(filtered)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReposUiState.Loading
    )

    init {
        loadRepositories()
    }

    /**
     * Aktualisiert den Suchtext für die Filterung der Repositories.
     *
     * @param newQuery Der neu eingegebene Suchbegriff.
     */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /**
     * Lädt die initiale Liste populärer GitHub-Repositories über das [repository].
     */
    private fun loadRepositories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.searchRepositories("stars:>10000")

            result.fold(
                onSuccess = { repos ->
                    _allRepos.value = repos
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Unbekannter Fehler beim Laden"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Lädt die nächste Seite der Repository-Ergebnisse (Paginierung) und hängt sie an die bestehende Liste an.
     */
    fun loadNextPage() {
        if (isNextPageLoading) return
        isNextPageLoading = true

        viewModelScope.launch {
            currentPage++
            val result = repository.searchRepositories(query = "stars:>10000", page = currentPage)

            result.fold(
                onSuccess = { newRepos ->
                    if (newRepos.isNotEmpty()) {
                        _allRepos.value = _allRepos.value + newRepos
                    }
                    isNextPageLoading = false
                },
                onFailure = {
                    isNextPageLoading = false
                }
            )
        }
    }

    /**
     * Liefert ein [RepositoryItem] aus dem aktuellen [ReposUiState.Success] anhand seiner ID.
     *
     * @param repoId Die eindeutige ID des gesuchten Repositories.
     * @return Das passende [RepositoryItem] oder `null`, falls nicht gefunden oder der Zustand nicht [ReposUiState.Success] ist.
     */
    fun getRepoById(repoId: Long): RepositoryItem? {
        val state = uiState.value
        return if (state is ReposUiState.Success) {
            state.repos.find { it.id == repoId }
        } else {
            null
        }
    }

    /**
     * Schaltet den Lesezeichen-Status für das übergebene Repository um.
     *
     * @param repo Das umzuschaltende [RepositoryItem].
     */
    fun toggleBookmark(repo: RepositoryItem) {
        viewModelScope.launch {
            repository.toggleRepoBookmark(repo)
        }
    }
}