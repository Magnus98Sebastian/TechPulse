package com.example.techpulse.domain

/**
 * Domänenmodell für einen Kommentar zu einem Artikel oder Beitrag.
 *
 * Repräsentiert die aufbereiteten Kommentardaten innerhalb der Anwendung,
 * unabhängig von der zugrundeliegenden Datenquelle (z. B. Dev.to API oder Firebase Cloud Firestore).
 *
 * @property id Eindeutige Kennung des Kommentars.
 * @property username Der Anzeigename des Verfassers.
 * @property timestamp Das Erstellungsdatum oder die vergangene Zeit seit Erstellung (z. B. "Gerade eben" oder ISO-Format).
 * @property contentText Der bereinigte Fließtext des Kommentars.
 * @property userAvatarUrl Die URL zum Profilbild des Verfassers (optional).
 */
data class Comment(
    val id: String,
    val username: String,
    val timestamp: String,
    val contentText: String,
    val userAvatarUrl: String? = null
)
