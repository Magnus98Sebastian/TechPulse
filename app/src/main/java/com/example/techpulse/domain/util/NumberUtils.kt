package com.example.techpulse.domain.util

import java.util.Locale

/**
 * Formatiert diese Ganzzahl in eine kompakte, lesbare Zeichenkette mit Tausender- (`k`)
 * und Millionen-Suffixen (`M`).
 *
 * ### Beispiele:
 * - `950` -> `"950"`
 * - `1_500` -> `"1.5k"` (bzw. `"1,5k"` je nach [Locale.getDefault])
 * - `2_500_000` -> `"2.5M"`
 *
 * @receiver Die zu formatierende Zahl [Int].
 * @return Ein formatierter [String] mit maximal einer Nachkommastelle bei Werten ab `1_000`.
 */
fun Int.toKFormattedString(): String {
    return when {
        this >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format(Locale.getDefault(), "%.1fk", this / 1_000.0)
        else -> this.toString()
    }
}