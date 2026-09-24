package com.example.techpulse.data.mapper

import com.example.techpulse.data.remote.dto.DevToCommentDto
import com.example.techpulse.domain.Comment

/**
 * Konvertiert ein [DevToCommentDto]-Datenübertragungsobjekt der Dev.to API
 * in das Domänenmodell [Comment].
 *
 * Im Rahmen der Konvertierung werden HTML-Tags aus dem Antworttext ([DevToCommentDto.bodyHtml])
 * mittels regulärem Ausdruck entfernt, um reinen Text für die Benutzeroberfläche bereitzustellen.
 *
 * @receiver Das von der REST-API empfangene [DevToCommentDto]-Objekt.
 * @return Das aufbereitete [Comment]-Domänenmodell.
 */
fun DevToCommentDto.toDomainModel(): Comment {
    val plainTextBody = bodyHtml
        ?.replace(Regex("<[^>]*>"), "")
        ?.trim() ?: ""

    return Comment(
        id = idCode,
        username = user?.name ?: "Unknown",
        timestamp = createdAt ?: "",
        contentText = plainTextBody,
        userAvatarUrl = user?.profileImage
    )
}