package com.example.techpulse.domain.util

import java.util.Locale

/**
 * Formatiert eine Ganzzahl in eine kompakte, lesbare Zeichenkette mit Tausender- (`k`)
 * und Millionen-Suffixen (`M`).
 *
 * Beispiele:
 * - `950` -> `"950"`
 * - `1500` -> `"1.5k"` (bzw. `"1,5k"` je nach [Locale])
 * - `2500000` -> `"2.5M"`
 *
 * @return Der formatierte String mit maximal einer Nachkommastelle bei Werten ab 1.000.
 */
fun Int.toKFormattedString(): String {
    return when {
        this >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", this / 1_000_000.0)
        this >= 1_000 -> String.format(Locale.getDefault(), "%.1fk", this / 1_000.0)
        else -> this.toString()
    }
}