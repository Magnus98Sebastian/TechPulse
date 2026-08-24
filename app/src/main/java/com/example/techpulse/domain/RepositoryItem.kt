package com.example.techpulse.domain

data class RepositoryItem(
    val id: Long,
    val name: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val description: String?,
    val starsCount: Int,
    val language: String?,
    val htmlUrl: String,
    val isBookmarked: Boolean = false
)
