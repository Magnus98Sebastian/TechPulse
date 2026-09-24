package com.example.techpulse.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Datenübertragungsobjekt (DTO) für Benutzer- und Autoreninformationen der Dev.to REST API.
 *
 * Dient der Deserialisierung von verschachtelten Benutzerdaten in Artikel-
 * und Kommentar-Antworten von Dev.to.
 *
 * @property name Der vollständige Anzeigename des Benutzers (Standard: „Anonymer Benutzer“).
 * @property username Der eindeutige Benutzername/Handle auf Dev.to (Standard: „unbekannt“).
 * @property profileImage Die URL zum Profilbild des Benutzers in optimierter Auflösung (90x90 Pixel).
 */
@Serializable
data class DevToUserDto(
    @SerialName("name") val name: String? = "Anonymer Benutzer",
    @SerialName("username") val username: String? = "unbekannt",
    @SerialName("profile_image_90") val profileImage: String? = null
)
