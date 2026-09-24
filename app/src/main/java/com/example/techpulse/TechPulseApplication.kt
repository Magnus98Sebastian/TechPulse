package com.example.techpulse

import android.app.Application
import com.example.techpulse.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.cloudinary.android.MediaManager

/**
 * Anwendungsweiter Einstiegspunkt ([Application]) der TechPulse-App.
 *
 * Verantwortlich für die globale Initialisierung von Frameworks und Bibliotheken
 * beim Start des Anwendungsprozesses:
 * - Konfiguration und Start des Koin Dependency Injection Frameworks ([startKoin]) mit den definierten Modulen ([appModule]).
 * - Initialisierung des Cloudinary [MediaManager] für den Upload und die Verwaltung von Medienressourcen.
 */
class TechPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@TechPulseApplication)
            modules(appModule)
        }

        val config = mapOf(
            "cloud_name" to "ljmkqmbe"
        )
        MediaManager.init(this,config)
    }
}