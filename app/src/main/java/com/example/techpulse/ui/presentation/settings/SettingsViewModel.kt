package com.example.techpulse.ui.presentation.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techpulse.data.repository.TechPulseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel zur Verwaltung der Benutzereinstellungen ([SettingsRoute]).
 *
 * Verwaltet das Laden und Speichern von Benutzernamen und Profilbild-URLs über das [TechPulseRepository].
 * Bietet zudem Funktionalitäten zur Bildkomprimierung und zum Upload des Profilbilds zu Cloudinary.
 *
 * @property repository Das Repository für den Zugriff auf lokal gespeicherte Einstellungen und Remote-Dienste.
 */
class SettingsViewModel(
    private val repository: TechPulseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)

    /** Der reaktive [StateFlow] des aktuellen [SettingsUiState]. */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Lädt die gespeicherten Benutzereinstellungen (Name und Profilbild-URL) aus dem [repository].
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val storedName = repository.getUsername() ?: ""
                val storedAvatar = repository.getUserAvatarUrl() ?: ""

                _uiState.value = SettingsUiState.Success(
                    username = storedName,
                    avatarUrl = storedAvatar
                )
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(
                    message = e.localizedMessage ?: "Unbekannter Fehler beim Laden"
                )
            }
        }
    }

    /**
     * Aktualisiert den Benutzernamen im aktuellen [SettingsUiState.Success].
     *
     * @param newName Der neu eingegebene Benutzername.
     */
    fun onUsernameChange(newName: String) {
        _uiState.update { currentState ->
            if (currentState is SettingsUiState.Success) {
                currentState.copy(username = newName, isSavedSuccessfully = false)
            } else currentState
        }
    }

    /**
     * Speichert den aktuellen Benutzernamen und die Profilbild-URL dauerhaft im [repository].
     */
    fun saveSettings() {
        val currentState = _uiState.value
        if (currentState is SettingsUiState.Success) {
            viewModelScope.launch {
                repository.saveUsername(currentState.username.trim())
                repository.saveUserAvatarUrl(currentState.avatarUrl.trim())

                _uiState.update {
                    (it as? SettingsUiState.Success)?.copy(isSavedSuccessfully = true) ?: it
                }
            }
        }
    }

    /**
     * Verarbeitet das Auswählen eines neuen Profilbilds.
     *
     * Aktualisiert sofort den lokalen UI-Zustand mit der gewählten [Uri], komprimiert das Bild
     * anschließend im Hintergrund und lädt es über Cloudinary hoch.
     *
     * @param context Der Android [Context] zum Auflösen der Bild-Uri und Erstellen temporärer Dateien.
     * @param uri Die [Uri] des ausgewählten Bildes.
     */
    fun onAvatarSelected(context: Context, uri: Uri) {
        val currentState = _uiState.value as? SettingsUiState.Success ?: return

        _uiState.update {
            currentState.copy(
                avatarUrl = uri.toString(),
                isSavedSuccessfully = false
            )
        }

        viewModelScope.launch {
            try {
                val compressedUri = compressAndResizeImage(context, uri)

                val uploadResult = repository.uploadAvatarToCloudinary(compressedUri)

                uploadResult.onSuccess { downloadUri ->
                    repository.saveUserAvatarUrl(downloadUri)

                    _uiState.update {
                        (it as? SettingsUiState.Success)?.copy(
                            avatarUrl = downloadUri,
                            isSavedSuccessfully = true
                        ) ?: it
                    }
                }.onFailure { error ->
                    Log.e("SettingsViewModel", "Hintergrund-Upload fehlgeschlagen", error)
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Fehler bei der Bildkomprimierung", e)
            }
        }
    }

    /**
     * Entfernt das Profilbild des Benutzers und setzt die Profilbild-URL im Speicher und Zustand zurück.
     */
    fun removeAvatar() {
        val currentState = _uiState.value as? SettingsUiState.Success ?: return

        _uiState.update {
            currentState.copy(
                avatarUrl = "",
                isSavedSuccessfully = false
            )
        }
         viewModelScope.launch{
             try {
                 repository.saveUserAvatarUrl("")
                 _uiState.update {
                     (it as? SettingsUiState.Success)?.copy(
                         isSavedSuccessfully = true
                     ) ?: it
                 }
             } catch (e: Exception) {
                 Log.e("SettingsViewModel", "Fehler beim Entfernen des Avatars", e)
             }
        }
    }

    /**
     * Skaliert und komprimiert ein ausgewähltes Bild auf max. 512px Kantenlänge und JPEG-Qualität 80%.
     *
     * @param context Der Android [Context] für ContentResolver und Cache-Verzeichnis.
     * @param uri Die [Uri] des Originalbildes.
     * @return Die [Uri] der temporär gespeicherten, komprimierten JPEG-Datei.
     */
    private fun compressAndResizeImage(context: Context, uri: Uri): Uri {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val maxSize = 512
        val width = originalBitmap.width
        val height = originalBitmap.height
        val bitmapRatio = width.toFloat() / height.toFloat()

        val (newWidth, newHeight) = if (bitmapRatio > 1) {
            maxSize to (maxSize / bitmapRatio).toInt()
        } else {
            (maxSize * bitmapRatio).toInt() to maxSize
        }

        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()

        val tempFile = File.createTempFile("compressed_image", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use {it.write(byteArray)}

        return Uri.fromFile(tempFile)
    }
}