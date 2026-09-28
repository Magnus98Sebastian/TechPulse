package com.example.techpulse.domain

/**
 * Repräsentiert das zentrale Domain-Modell für einen Kommentar zu einem Artikel oder Beitrag.
 *
 * Repräsentiert die aufbereiteten Kommentardaten innerhalb der Anwendung,
 * unabhängig von der zugrundeliegenden Datenquelle (z. B. DEV.to API oder Firebase Cloud Firestore).
 *
 * @property id Eindeutige Kennung des Kommentars.
 * @property username Der Anzeigename des Verfassers.
 * @property timestamp Formatiertes Erstellungsdatum oder Zeitabstand seit Erstellung (z. B. "Gerade eben" oder ISO-8601).
 * @property contentText Der bereinigte Fließtext des Kommentars.
 * @property userAvatarUrl Web-URL zum Profilbild des Verfassers (optional, sonst `null`).
 */
data class Comment(
    val id: String,
    val username: String,
    val timestamp: String,
    val contentText: String,
    val userAvatarUrl: String? = null
)
