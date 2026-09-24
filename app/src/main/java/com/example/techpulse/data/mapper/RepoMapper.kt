package com.example.techpulse.data.mapper

import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.data.remote.dto.GithubRepoDto

/**
 * Konvertiert ein [GithubRepoDto]-Datenübertragungsobjekt der GitHub REST API
 * in das Domänenmodell [RepositoryItem].
 *
 * Behandelt optionale Felder der API (wie Beschreibung und Programmiersprache)
 * mit entsprechenden Fallback-Werten für die Benutzeroberfläche und setzt den
 * initialen Lesezeichen-Status ([RepositoryItem.isBookmarked]) auf `false`.
 *
 * @receiver Das von der GitHub API empfangene [GithubRepoDto]-Objekt.
 * @return Das aufbereitete [RepositoryItem]-Domänenmodell.
 */
fun GithubRepoDto.toDomainModel(): RepositoryItem {
    return RepositoryItem(
        id = id,
        name = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        description = description ?: "Keine Beschreibung verfügbar",
        starsCount = starsCount,
        language = language ?: "Unbekannt",
        htmlUrl = htmlUrl ?: "",
        isBookmarked = false
    )
}