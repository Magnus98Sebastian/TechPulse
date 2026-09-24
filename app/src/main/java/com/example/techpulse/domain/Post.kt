package com.example.techpulse.domain

/**
 * Domänenmodell für einen Community-Beitrag innerhalb der Anwendung.
 *
 * Repräsentiert von Benutzern erstellte Posts für Feeds und Interaktionsansichten.
 *
 * @property id Eindeutige Kennung des Beitrags.
 * @property username Der Anzeigename des Autors.
 * @property userAvatarUrl Die URL zum Profilbild des Autors (optional).
 * @property timestamp Das Erstellungsdatum oder die verstrichene Zeit seit der Veröffentlichung.
 * @property contentText Der Fließtext oder Hauptinhalt des Beitrags.
 * @property imageUrl Die URL eines angehängten Bildes (optional).
 * @property likeCount Die Gesamtzahl der Vergaben von „Gefällt mir“-Markierungen.
 * @property commentCount Die Gesamtzahl der Kommentare zu diesem Beitrag.
 * @property isLiked Gibt an, ob der aktuell angemeldete Benutzer diesen Beitrag gelikt hat.
 * @property isBookmarked Gibt an, ob der Beitrag vom aktuellen Benutzer als Lesezeichen gespeichert wurde.
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
