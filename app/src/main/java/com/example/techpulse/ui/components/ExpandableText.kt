package com.example.techpulse.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Eine Jetpack Compose UI-Komponente zur Darstellung von langem Text, der bei Bedarf
 * ein- und ausgeklappt werden kann.
 *
 * Wenn der Text die maximale Zeilenanzahl im eingeklappten Zustand ([collapsedMaxLines]) überschreitet,
 * wird automatisch ein klickbarer Button ("Mehr anzeigen..." / "Weniger anzeigen") eingeblendet.
 * Der Übergang zwischen den Zuständen ist mittels [animateContentSize] sanft animiert.
 *
 * @param text Der anzuzeigende Volltext.
 * @param modifier Der [Modifier] zur externen Layout-Konfiguration.
 * @param collapsedMaxLines Die maximale Anzahl an Zeilen im eingeklappten Zustand (Standard: 4).
 */
@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 4
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded) {
                    isClickable = textLayoutResult.hasVisualOverflow
                }
            }
        )
        if (isClickable || isExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isExpanded) "Weniger anzeigen" else "Mehr anzeigen...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
            )
        }
    }
}