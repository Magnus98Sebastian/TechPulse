package com.example.techpulse.domain

/**
 * Repräsentiert das zentrale Domain-Modell für einen News-Artikel oder Blog-Beitrag.
 *
 * @property id Eindeutige Kennung des Artikels.
 * @property title Der Haupttitel des Artikels.
 * @property description Kurze Zusammenfassung oder Vorschautext des Artikels (optional, sonst `null`).
 * @property bodyText Der vollständige Fließtext oder Hauptinhalt des Beitrags.
 * @property sourceName Name der Quellplattform (z. B. "DEV Community").
 * @property publishedAt Formatiertes Veröffentlichungsdatum oder Zeitstempel (z. B. ISO-8601 oder "2 hours ago").
 * @property imageUrl Web-URL des Beitrags- oder Headerbildes (optional).
 * @property userAvatarUrl Web-URL des Autoren-Profilbildes (optional).
 * @property isLiked Gibt an, ob der aktuelle Benutzer den Artikel gelikt hat.
 * @property likeCount Gesamtzahl der vergebenen Likes oder Herzen.
 * @property publicReactionsCount Gesamtzahl aller öffentlichen Reaktionen.
 * @property commentCount Anzahl der Kommentare unter dem Artikel.
 * @property isBookmarked Gibt an, ob der Artikel lokal als Lesezeichen gespeichert ist.
 */
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val bodyText: String = "",
    val sourceName: String?,
    val publishedAt: String,
    val imageUrl: String?,
    val userAvatarUrl: String? = null,
    val isLiked: Boolean = false,
    val likeCount: Int = 0,
    val publicReactionsCount: Int = 0,
    val commentCount: Int = 0,
    val isBookmarked: Boolean = false
)
