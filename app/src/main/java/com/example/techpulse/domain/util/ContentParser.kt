package com.example.techpulse.domain.util

/**
 * Repräsentiert das Ergebnis des geparsten Beitragsinhalts.
 *
 * @property title Der extrahierte Titel aus dem Front-Matter (falls vorhanden), sonst ein leerer String.
 * @property cleanBody Der bereinigte Haupttext des Beitrags ohne den Front-Matter-Metadaten-Block.
 */
data class ParsedPostContent(
    val title: String,
    val cleanBody: String
)

/**
 * Hilfsobjekt zum Verarbeiten und Bereinigen von rohem Beitragsinhalt (z. B. Markdown mit YAML-Front-Matter).
 */
object ContentParser {

    // Regex zur exakten Trennung von Front-Matter und Body am Anfang der Datei
    private val frontMatterRegex = Regex("""^---\r?\n([\s\S]*?)\r?\n---\r?\n?""")

    // Regex zur Extraktion des Titels innerhalb des Front-Matter-Blocks
    private val titleRegex = Regex("""(?m)^title:\s*["']?([^"'\n\r]+)["']?""")

    /**
     * Parst den rohen Text eines Beitrags und trennt YAML-Front-Matter-Metadaten vom eigentlichen Inhalt.
     *
     * @param rawText Der unbearbeitete Gesamttext des Beitrags.
     * @return Ein [ParsedPostContent]-Objekt mit extrahiertem Titel und bereinigtem Haupttext.
     */
    fun parseContentText(rawText: String): ParsedPostContent {
        if (!rawText.startsWith("---")) {
            return ParsedPostContent(title = "", cleanBody = rawText)
        }

        val frontMatterMatch = frontMatterRegex.find(rawText)
            ?: return ParsedPostContent(title = "", cleanBody = rawText)

        // Nur im isolierten Front-Matter-Header nach dem Titel suchen
        val headerContent = frontMatterMatch.groupValues[1]
        val extractedTitle = titleRegex.find(headerContent)?.groupValues?.get(1)?.trim().orEmpty()

        // Nur den vorderen Header entfernen
        val cleanedText = rawText.substring(frontMatterMatch.range.last + 1).trim()

        return ParsedPostContent(
            title = extractedTitle,
            cleanBody = cleanedText
        )
    }
}