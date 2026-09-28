package com.example.techpulse.domain

/**
 * Repräsentiert das zentrale Domain-Modell für ein GitHub-Repository.
 *
 * Enthält relevante Detail- und Metadaten eines Repositories (z. B. aus der GitHub-REST-API)
 * für die Anzeige in Feeds, Suchergebnissen und Lesezeichen.
 *
 * @property id Eindeutige Repository-ID.
 * @property name Der Name des Repositories.
 * @property ownerName Der Benutzername oder Organisationsname des Eigentümers.
 * @property ownerAvatarUrl Web-URL zum Profilbild/Avatar des Eigentümers (optional, sonst `null`).
 * @property description Kurze Beschreibung des Repositories (optional, sonst `null`).
 * @property language Die primäre Programmiersprache des Projekts (optional, sonst `null`).
 * @property starsCount Gesamtzahl der vergebenen Stars (Sterne).
 * @property forksCount Gesamtzahl der erstellten Forks.
 * @property openIssuesCount Die Anzahl der aktuell offenen Issues.
 * @property watchersCount Die Anzahl der Personen, die das Repository beobachten.
 * @property licenseName Der Name der verwendeten Open-Source-Lizenz (optional, sonst `null`).
 * @property defaultBranch Der Haupt-Branch des Repositories (Standard: `"main"`).
 * @property htmlUrl Die direkte Web-URL zum Repository auf GitHub (optional, sonst `null`).
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
     * Die direkte Web-URL zur README-Sektion des Repositories.
     *
     * Basiert auf [htmlUrl] mit angehängtem Anchor `#readme`. Gibt `null` zurück,
     * falls [htmlUrl] `null` oder leer ist.
     */
    val readmeUrl: String?
        get() = if (!htmlUrl.isNullOrBlank()) {
            "$htmlUrl#readme"
        } else null
}
