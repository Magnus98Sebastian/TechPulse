package com.example.techpulse.domain.util

import androidx.compose.ui.graphics.Color

/**
 * Liefert den passenden Farbwert [Color] für eine gegebene Programmier- oder Auszeichnungssprache.
 *
 * Die Zuordnung orientiert sich an den Standard-Farben gängiger Plattformen (wie GitHub).
 * Ist [language] `null` oder unbekannt, wird ein neutraler Standard-Grauton zurückgegeben.
 *
 * @param language Der Name oder das Kürzel der Sprache (z. B. "Kotlin", "js", "c++"). Groß-/Kleinschreibung wird ignoriert.
 * @return Die entsprechende [Color] für UI-Elemente wie Badges oder Chips.
 */
fun getLanguageColor(language: String?): Color {
    return when (language?.lowercase()) {
        "kotlin" -> Color(0xFF7F52FF)
        "java" -> Color(0xFFB07219)
        "swift" -> Color(0xFFF05138)
        "javascript", "js" -> Color(0xFFF1E05A)
        "typescript", "ts" -> Color(0xFF3178C6)
        "python" -> Color(0xFF3572A5)
        "c++", "cpp" -> Color(0xFFF34B7D)
        "c#" -> Color(0xFF178600)
        "html" -> Color(0xFFE34C26)
        "rust" -> Color(0xFFDEA584)
        "go" -> Color(0xFF00ADD8)
        else -> Color(0xFF8B949E)
    }
}

