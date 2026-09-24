package com.example.techpulse.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Datenübertragungsobjekt (DTO) für einen Artikel der Dev.to REST API.
 *
 * Dient der Deserialisierung von JSON-Antworten bei Schnittstellenanfragen an Dev.to.
 *
 * @property id Die eindeutige Kennung des Artikels.
 * @property title Der Titel des Artikels.
 * @property description Die Kurzbeschreibung oder Vorschau des Artikels.
 * @property bodyMarkdown Der vollständige Artikelinhalt im Markdown-Format.
 * @property bodyHtml Der Artikelinhalt als aufbereiteter HTML-String.
 * @property publishedAt Das Veröffentlichungsdatum in lesbarer Formatierung.
 * @property coverImage Die URL des Haupt-Titelbildes (falls vorhanden).
 * @property socialImage Die URL des Bildes für soziale Medien/Vorschauen.
 * @property publicReactionsCount Die Gesamtzahl der öffentlichen Reaktionen (Likes, Fire, etc.).
 * @property commentsCount Die Anzahl der Kommentare zum Artikel.
 * @property user Das verschachtelte [DevToUserDto]-Objekt mit Informationen zum Autor.
 */
@Serializable
data class DevToArticleDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("body_markdown") val bodyMarkdown: String? = null,
    @SerialName("body_html") val bodyHtml: String? = null,
    @SerialName("readable_publish_date") val publishedAt: String? = null,
    @SerialName("cover_image") val coverImage: String? = null,
    @SerialName("social_image") val socialImage: String? = null,
    @SerialName("public_reactions_count") val publicReactionsCount: Int? = null,
    @SerialName("comments_count") val commentsCount: Int? = null,
    @SerialName("user") val user: DevToUserDto? = null
)
