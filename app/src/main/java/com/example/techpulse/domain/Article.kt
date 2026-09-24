package com.example.techpulse.domain

/**
* Domain-Modell für einen News-Artikel oder Blog-Beitrag in der TechPulse-App.
*
* @property id Eindeutige Kennung des Artikels.
* @property title Der Haupttitel des Artikels.
* @property description Kurze Zusammenfassung oder Vorschautext des Artikels (optional).
* @property bodyText Der vollständige Fließtext oder Inhalt des Beitrags.
* @property sourceName Name der Quelle oder der Plattform (z. B. "DEV Community").
* @property publishedAt Formatiertes Datum oder Zeitstempel der Veröffentlichung.
* @property imageUrl URL des Beitrags- oder Bannerbildes (optional).
* @property userAvatarUrl URL des Profilbildes des Autors (optional).
* @property isLiked Gibt an, ob der aktuelle Benutzer den Artikel gelikt hat.
* @property likeCount Gesamtzahl der Vergaben von Likes/Herzen.
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
