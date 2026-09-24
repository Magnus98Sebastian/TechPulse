package com.example.techpulse.domain.util

/**
 * Repräsentiert das Ergebnis des geparsten Beitragsinhalts.
 *
 * @property title Der extrahierte Titel aus dem Front-Matter (falls vorhanden), sonst ein leerer String.
 * @property cleanBody Der Bereinigte Haupttext des Beitrags ohne das Front-Matter-Metadaten-Block.
 */
data class ParsedPostContent(
    val title: String,
    val cleanBody: String
)

/**
 * Hilfsobjekt zum Verarbeiten und Bereinigen von rohem Beitragsinhalt (z. B. Markdown mit YAML-Front-Matter).
 */
object ContentParser {

    /**
     * Parsed den rohen Text eines Beitrags und trennt YAML-Front-Matter-Metadaten vom eigentlichen Inhalt.
     *
     * Falls der Text mit Front-Matter (`---`) beginnt, wird der Titel über eine Reguläre Expression
     * extrahiert und der Metadaten-Block aus dem Rumpftext entfernt.
     *
     * @param rawText Der unbearbeitete Gesamttext des Beitrags.
     * @return Ein [ParsedPostContent]-Objekt mit extrahiertem Titel und bereinigtem Haupttext.
     */
    fun parseContentText(rawText: String): ParsedPostContent {
        if (!rawText.startsWith("---")) {
            return ParsedPostContent(title = "", cleanBody = rawText)
        }

        val titleRegex = Regex("""title:\s*["']?([^"\n\r]+)["']?""")
        val titleMatch = titleRegex.find(rawText)
        val extractedTitle = titleMatch?.groupValues?.get(1)?.trim() ?: ""

        val cleanedText = rawText.replace(Regex("""^---[\s\S]*?---\s*"""), "").trim()

        return ParsedPostContent(
            title = extractedTitle,
            cleanBody = cleanedText
        )
    }
}