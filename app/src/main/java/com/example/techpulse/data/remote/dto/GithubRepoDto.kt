package com.example.techpulse.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubSearchResponseDto(
    @SerialName("items") val items: List<GithubRepoDto>
)

@Serializable
data class GithubRepoDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("stargazers_count") val starsCount: Int,
    @SerialName("language") val language: String?,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("owner") val owner: GithubOwnerDto
)

@Serializable
data class GithubOwnerDto(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)