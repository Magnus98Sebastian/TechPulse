package com.example.techpulse.domain

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val sourceName: String,
    val isBookmarked: Boolean = false
)
