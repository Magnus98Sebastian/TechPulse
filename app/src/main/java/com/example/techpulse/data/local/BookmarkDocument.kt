package com.example.techpulse.data.local

import com.google.firebase.firestore.PropertyName

/**
 * Repräsentiert ein gespeichertes Lesezeichen (Bookmark) in Cloud Firestore.
 *
 * Diese Datenklasse dient als Transferobjekt (DTO) für das Auslesen und Schreiben
 * von Lesezeichen-Dokumenten in der Firestore-Datenbank.
 *
 * @property id Eindeutige ID des Lesezeichens.
 * @property title Der Titel des gespeicherten Artikels oder Beitrags.
 * @property description Eine optionale Kurzbeschreibung oder ein Vorschautext des Inhalts.
 * @property imageUrl Die URL zum Vorschaubild des Artikels.
 * @property userAvatarUrl Die optionale URL zum Profilbild des Erstellers/Autors.
 * @property createdAt Der Erstellungszeitpunkt des Lesezeichens als UTC-Timestamp in Millisekunden.
 * @property sourceName Der Name der Quelle oder des Herkunftsmediums (z. B. Magazin oder Website).
 * @property likeCount Die aktuelle Anzahl der Gefällt-mir-Angaben.
 * @property commentCount Die aktuelle Anzahl der Kommentare.
 * @property isLiked Gibt an, ob der aktuelle Benutzer diesen Beitrag mit „Gefällt mir“ markiert hat.
 * @property type Der Typ des Lesezeichens (z. B. "POST" oder "REPOSITORY").
 * @property language Die verwendete Hauptprogrammiersprache (falls es sich um ein Repository handelt).
 */
data class BookmarkDocument(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("title") @set:PropertyName("title") var title: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String? = null,
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = "",
    @get:PropertyName("userAvatarUrl") @set:PropertyName("userAvatarUrl") var userAvatarUrl: String? = null,
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("sourceName") @set:PropertyName("sourceName") var sourceName: String = "",
    @get:PropertyName("likeCount") @set:PropertyName("likeCount") var likeCount: Int = 0,
    @get:PropertyName("commentCount") @set:PropertyName("commentCount") var commentCount: Int = 0,
    @get:PropertyName("isLiked") @set:PropertyName("isLiked") var isLiked: Boolean = false,
    @get:PropertyName("type") @set:PropertyName("type") var type: String = "POST",
    @get:PropertyName("language") @set:PropertyName("language") var language: String? = null
)