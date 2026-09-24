package com.example.techpulse.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Datenübertragungsobjekt (DTO) für einen Kommentar der Dev.to REST API.
 *
 * Dient der Deserialisierung der JSON-Antworten bei Schnittstellenanfragen
 * für Artikelkommentare von Dev.to.
 *
 * @property idCode Die eindeutige Kennung des Kommentars.
 * @property bodyHtml Der Inhalt des Kommentars als HTML-String.
 * @property user Das verschachtelte [DevToUserDto]-Objekt mit Informationen zum Verfasser des Kommentars.
 * @property createdAt Der Erstellungszeitpunkt des Kommentars als ISO-8601-Zeitstempel-String.
 */
@Serializable
data class DevToCommentDto(
    @SerialName("id_code") val idCode: String,
    @SerialName("body_html") val bodyHtml: String? = null,
    @SerialName("user") val user: DevToUserDto? = null,
    @SerialName("created_at") val createdAt: String? = null
)