package com.example.techpulse.data.mapper

import androidx.core.text.HtmlCompat
import com.example.techpulse.data.remote.dto.DevToArticleDto
import com.example.techpulse.domain.Article
import com.example.techpulse.domain.Post

/**
 * Konvertiert ein [DevToArticleDto]-Datenübertragungsobjekt (DTO) der REST-API
 * in das Domänenmodell [Article] der Anwendung.
 *
 * Im Rahmen der Konvertierung werden HTML-Tags und Formatierungen aus dem Artikeltext
 * sowie der Beschreibung mithilfe von [HtmlCompat] bereinigt und Rückfallwerte (Fallback-Values)
 * für fehlende Eigenschaften gesetzt.
 *
 * @receiver [DevToArticleDto] Das von der Dev.to API empfangene Datenobjekt.
 * @return Ein instanziiertes [Article]-Domänenobjekt mit bereinigten Textinhalten.
 */
fun DevToArticleDto.toDomainModel(): Article {
    val rawText = bodyMarkdown ?: bodyHtml ?: description ?: ""
    val cleanedBodyText = HtmlCompat.fromHtml(rawText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        .toString()
        .trim()

    val cleanedText = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        .toString()
        .trim()

    return Article(
        id = id.toString(),
        title = title,
        description = cleanedText,
        bodyText = cleanedBodyText,
        sourceName = user?.name ?: "Dev.to",
        publishedAt = publishedAt ?: "Unbekanntes Datum",
        imageUrl = coverImage ?: socialImage ?: "",
        likeCount = publicReactionsCount ?: 0,
        publicReactionsCount = publicReactionsCount ?: 0,
        commentCount = commentsCount ?: 0,
        userAvatarUrl = user?.profileImage ?: ""
    )
}

/**
 * Konvertiert ein [Article]-Domänenmodell in ein [Post]-Objekt für die Feed-Darstellung.
 *
 * Fügt Titel und Beschreibung zu einem zusammenhängenden Inhaltstext zusammen und setzt
 * Standardwerte für UI-Zustände wie Likes und Lesezeichen.
 *
 * @receiver [Article] Das Quell-Artikelobjekt aus der Domänenschicht.
 * @return Ein instanziiertes [Post]-Objekt zur Anzeige in der Feeds-Übersicht.
 */
fun Article.toPost(): Post {
    return Post(
        id = this.id.toString(),
        username = this.sourceName ?: "",
        userAvatarUrl = this.userAvatarUrl,
        timestamp = this.publishedAt ?: "",
        contentText = if (this.description.isNullOrEmpty()) this.title else "${this.title}\n\n${this.description}",
        imageUrl = this.imageUrl,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        isLiked = false,
        isBookmarked = false
    )
}