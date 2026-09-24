package com.example.techpulse.domain

/**
 * Domänenmodell für ein GitHub-Repository innerhalb der Anwendung.
 *
 * Enthält relevante Detail- und Metadaten eines Repositories (z. B. aus der GitHub REST API)
 * für die Anzeige in Listen, Suchergebnissen und Lesezeichen.
 *
 * @property id Eindeutige Repository-ID.
 * @property name Der Name des Repositories.
 * @property ownerName Der Benutzername oder Organisationsname des Eigentümers.
 * @property ownerAvatarUrl Die URL zum Profilbild/Avatar des Eigentümers (optional).
 * @property description Die Beschreibung des Repositories (optional).
 * @property language Die primäre Programmiersprache des Projekts (optional).
 * @property starsCount Die Anzahl der vergebenen Stars.
 * @property forksCount Die Anzahl der Erstellungen von Forks.
 * @property openIssuesCount Die Anzahl der aktuell offenen Issues.
 * @property watchersCount Die Anzahl der Personen, die das Repository beobachten.
 * @property licenseName Der Name der verwendeten Open-Source-Lizenz (optional).
 * @property defaultBranch Der Haupt-Branch des Repositories (Standard: `"main"`).
 * @property htmlUrl Die direkte Web-URL zum Repository auf GitHub (optional).
 * @property topics Eine Liste von Tags/Themenzuordnungen des Repositories.
 * @property isBookmarked Gibt an, ob das Repository lokal als Lesezeichen gespeichert wurde.
 */
data class RepositoryItem(
    val id: Long,
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String? = null,
    val description: String?,
    val language: String?,
    val starsCount: Int,
    val forksCount: Int = 0,
    val openIssuesCount: Int = 0,
    val watchersCount: Int = 0,
    val licenseName: String? = null,
    val defaultBranch: String? = "main",
    val htmlUrl: String? = "",
    val topics: List<String> = emptyList(),
    val isBookmarked: Boolean = false
) {
    /**
     * Generiert die Web-URL zur README-Sektion des Repositories,
     * sofern eine gültige [htmlUrl] vorhanden ist.
     */
    val readmeUrl: String?
        get() = if (!htmlUrl.isNullOrBlank()) {
            "$htmlUrl#readme"
        } else null
}
