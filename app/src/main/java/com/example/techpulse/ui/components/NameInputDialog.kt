package com.example.techpulse.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

/**
 * Ein Material Design 3 Dialog ([AlertDialog]) zur Eingabe eines Benutzernamens.
 *
 * Bietet dem Benutzer ein Eingabefeld ([OutlinedTextField]) sowie Validierungsunterstützung
 * über [errorMessage]. Falls das Feld leer bleibt, bietet der Bestätigungs-Button dynamisch
 * die Option, einen zufälligen Namen zu generieren/nutzen.
 *
 * @param errorMessage Optionale Fehlermeldung zur Validierung (z. B. bei unzulässigen Zeichen oder bereits vergebenem Namen). `null`, wenn kein Fehler vorliegt.
 * @param onDismiss Callback, der aufgerufen wird, wenn der Dialog abgebrochen oder außerhalb geklickt wird.
 * @param onConfirm Callback, der beim Bestätigen aufgerufen wird. Übergibt den aktuell eingegebenen Text (kann leer sein).
 */
@Composable
fun NameInputDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Benutzername eingeben") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Bitte gib deinen Benutzernamen ein.")

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text(text = "Benutzername") },
                    isError = errorMessage != null,
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(textInput)
                }
            ) {
                Text(if (textInput.isBlank()) "Zufälligen Namen nutzen" else "Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Abbrechen")
            }
        }
    )
}