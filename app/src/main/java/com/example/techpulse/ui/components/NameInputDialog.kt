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
 * Repräsentiert einen Material Design 3 Dialog ([AlertDialog]) zur Eingabe eines Benutzernamens.
 *
 * Bietet dem Benutzer ein Eingabefeld ([OutlinedTextField]) sowie Validierungsunterstützung
 * über [errorMessage]. Bleibt das Feld leer, schlägt die Bestätigungsschaltfläche
 * dynamisch die Verwendung eines zufälligen Namens vor.
 *
 * @param errorMessage Optionale Fehlermeldung zur Validierung (z. B. bei unzulässigen Zeichen oder bereits vergebenem Namen), sonst `null`.
 * @param onDismiss Lambda-Callback, der beim Abbrechen oder Schließen des Dialogs aufgerufen wird.
 * @param onConfirm Lambda-Callback, der beim Bestätigen aufgerufen wird und den eingegebenen Text übergibt (kann leer sein).
 * @param modifier Der optional anwendbare [Modifier] für Layout-Anpassungen von außen.
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