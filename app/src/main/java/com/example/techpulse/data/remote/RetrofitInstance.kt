package com.example.techpulse.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Singleton-Objekt zur Bereitstellung und Konfiguration der Retrofit-Instanz.
 *
 * Verwaltet die zentrale HTTP-Schnittstellenkonfiguration für Netzwerkanfragen,
 * einschließlich des JSON-Parsers (`kotlinx.serialization`) und der Basis-URL.
 */
object RetrofitInstance {

    /**
     * Zuweisung und Konfiguration des [Json]-Parsers von `kotlinx.serialization`.
     *
     * Konfigurationseigenschaften:
     * - [JsonBuilder.ignoreUnknownKeys]: Ignoriert nicht definierte Felder in den JSON-Antworten der API.
     * - [JsonBuilder.isLenient]: Erlaubt flexibleres Parsen von nicht streng spezifiziertem JSON.
     * - [JsonBuilder.coerceInputValues]: Erzwingt bei ungültigen Werten oder `null` das Zurückfallen auf Default-Werte.
     */
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Träge (lazy) initialisierte Instanz des [TechPulseApi]-Service-Interfaces.
     *
     * Die Erstellung der Retrofit-Instanz erfolgt erst beim ersten Zugriff und nutzt
     * die definierte [TechPulseApi.DEV_TO_BASE_URL] sowie den [json]-Konverter.
     */
    val api: TechPulseApi by lazy {
        Retrofit.Builder()
            .baseUrl(TechPulseApi.DEV_TO_BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TechPulseApi::class.java)
    }
}