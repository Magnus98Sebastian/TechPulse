package com.example.techpulse.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Datenübertragungsobjekt (DTO) für die Antwort der GitHub-Suchschnittstelle.
 *
 * @property items Die Liste der gefundenen GitHub-Repositorys ([GithubRepoDto]).
 */
@Serializable
data class GithubSearchResponseDto(
    @SerialName("items") val items: List<GithubRepoDto>
)

/**
 * Datenübertragungsobjekt (DTO) für ein einzelnes GitHub-Repository.
 *
 * Dient der Deserialisierung von Repository-Informationen der GitHub REST API.
 *
 * @property id Die eindeutige Kennung des Repositorys.
 * @property name Der Name des Repositorys.
 * @property description Die optionale Beschreibung des Repositorys.
 * @property starsCount Die Anzahl der vergebenen Sterne (Stargazers).
 * @property language Die primäre Programmiersprache des Repositorys (falls erkannt).
 * @property htmlUrl Der direkte Link zur Webansicht des Repositorys auf GitHub.
 * @property owner Das verschachtelte [GithubOwnerDto]-Objekt mit Informationen zum Eigentümer.
 */
@Serializable
data class GithubRepoDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("stargazers_count") val starsCount: Int,
    @SerialName("language") val language: String?,
    @SerialName("html_url") val htmlUrl: String?,
    @SerialName("owner") val owner: GithubOwnerDto
)

/**
 * Datenübertragungsobjekt (DTO) für den Eigentümer (Benutzer oder Organisation) eines GitHub-Repositorys.
 *
 * @property login Der Benutzer- bzw. Organisationsname auf GitHub.
 * @property avatarUrl Die URL zum Profilbild des Eigentümers.
 */
@Serializable
data class GithubOwnerDto(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)