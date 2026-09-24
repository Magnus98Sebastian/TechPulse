package com.example.techpulse.data.mapper

import com.example.techpulse.data.local.BookmarkDocument
import com.example.techpulse.domain.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
* Konvertiert ein [BookmarkDocument] aus der lokalen Firestore-Datenbank
* in ein [Post]-Domänenmodell für die Benutzeroberfläche.
*
* Konvertierungslogik:
* - Wandelt den Unix-Timestamp [BookmarkDocument.createdAt] in ein lesbares Datumsformat (`dd.MM.yyyy`) um.
* - Fügt Titel und Beschreibung zusammen, falls eine Beschreibung vorhanden und ungleich dem Titel ist.
* - Setzt [Post.isBookmarked] fest auf `true`.
*
* @receiver Das zu konvertierende [BookmarkDocument]-Objekt.
* @return Das aufbereitete [Post]-Domänenmodell.
*/
fun BookmarkDocument.toPost(): Post {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val formattedDate = formatter.format(Date(this.createdAt))

    val content = if (description.isNullOrEmpty() || description == title) {
        title
    } else {
        "$title\n\n$description"
    }

    return Post(
        id = this.id,
        username = this.sourceName,
        userAvatarUrl = this.userAvatarUrl,
        timestamp = formattedDate,
        contentText = content,
        imageUrl = this.imageUrl,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        isLiked = this.isLiked,
        isBookmarked = true
    )
}

/**
 * Konvertiert ein [Post]-Domänenmodell in ein [BookmarkDocument] zur Speicherung in Firestore.
 *
 * Konvertierungslogik:
 * - Übernimmt den Beitragstext als Titel des Dokuments.
 * - Setzt den Erstellungszeitpunkt ([BookmarkDocument.createdAt]) auf den aktuellen Systemzeitstempel.
 *
 * @receiver Das zu konvertierende [Post]-Objekt.
 * @return Das für die Firestore-Datenbank vorbereitete [BookmarkDocument].
 */
fun Post.toBookmarkDocument(): BookmarkDocument {
    return BookmarkDocument(
        id = this.id,
        title = this.contentText,
        description = null,
        imageUrl = this.imageUrl ?: "",
        userAvatarUrl = this.userAvatarUrl,
        createdAt = System.currentTimeMillis(),
        sourceName = this.username,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        isLiked = this.isLiked,
        type = "POST"
    )
}