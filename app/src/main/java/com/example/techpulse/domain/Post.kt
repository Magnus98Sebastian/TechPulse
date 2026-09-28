package com.example.techpulse.domain

/**
 * Repräsentiert das zentrale Domain-Modell für einen Community-Beitrag.
 *
 * Wird für Feeds, Detailansichten und Benutzerinteraktionen innerhalb der Anwendung verwendet.
 *
 * @property id Eindeutige Kennung des Beitrags.
 * @property username Der Anzeigename des Autors.
 * @property userAvatarUrl Web-URL zum Profilbild des Autors (optional, sonst `null`).
 * @property timestamp Formatiertes Erstellungsdatum oder Zeitabstand seit Veröffentlichung (z. B. "Vor 5 Min." oder ISO-8601).
 * @property contentText Der Fließtext oder Hauptinhalt des Beitrags.
 * @property imageUrl Web-URL eines angehängten Beitragsbildes (optional, sonst `null`).
 * @property likeCount Gesamtzahl der vergebenen Likes oder Herzen.
 * @property commentCount Die Gesamtzahl der Kommentare zu diesem Beitrag.
 * @property isLiked Gibt an, ob der aktuelle Benutzer den Beitrag gelikt hat.
 * @property isBookmarked Gibt an, ob der Beitrag vom aktuellen Benutzer als Lesezeichen gespeichert ist.
 */
data class Post(
    val id: String,
    val username: String,
    val userAvatarUrl: String? = null,
    val timestamp: String,
    val contentText: String,
    val imageUrl: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false
)
