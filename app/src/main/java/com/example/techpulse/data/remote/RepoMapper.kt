package com.example.techpulse.data.remote

import com.example.techpulse.domain.RepositoryItem
import com.example.techpulse.data.remote.dto.GithubRepoDto

fun GithubRepoDto.toDomainModel(): RepositoryItem {
    return RepositoryItem(
        id = id,
        name = name,
        ownerName = owner.login,
        ownerAvatarUrl = owner.avatarUrl,
        description = description ?: "Keine Beschreibung verfügbar",
        starsCount = starsCount,
        language = language ?: "Unbekannt",
        htmlUrl = htmlUrl,
        isBookmarked = false
    )
}