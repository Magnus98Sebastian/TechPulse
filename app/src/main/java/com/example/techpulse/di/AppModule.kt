package com.example.techpulse.di

import com.example.techpulse.data.remote.TechPulseApi
import com.example.techpulse.data.repository.TechPulseRepository
import com.example.techpulse.ui.main.MainViewModel
import com.example.techpulse.ui.presentation.bookmarks.BookmarksViewModel
import com.example.techpulse.ui.presentation.feed.FeedViewModel
import com.example.techpulse.ui.presentation.postDetail.PostDetailViewModel
import com.example.techpulse.ui.presentation.repos.ReposViewModel
import com.example.techpulse.ui.presentation.settings.SettingsViewModel
import com.example.techpulse.ui.presentation.user.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Zentraoles Dependency Injection (DI) Modul der Anwendung mittels Koin DSL.
 *
 * Konfiguriert und verwaltet die Lebenszyklen folgender Abhängigkeiten:
 * - Firebase-Dienste ([FirebaseFirestore], [FirebaseAuth])
 * - Netzwerk- und Parsing-Komponenten ([Json], [OkHttpClient], [Retrofit], [TechPulseApi])
 * - Daten-Repositorys ([TechPulseRepository])
 * - UI-ViewModels (z. B. [FeedViewModel], [BookmarksViewModel], [UserProfileViewModel])
 */
val appModule = module {

    /** Bereitstellung der Singleton-Instanz von [FirebaseFirestore]. */
    single { FirebaseFirestore.getInstance() }

    /** Bereitstellung der Singleton-Instanz von [FirebaseAuth]. */
    single { FirebaseAuth.getInstance() }

    /**
     * Konfiguration des [Json]-Parsers von `kotlinx.serialization`.
     * Erlaubt unbekannte Schlüssel, nachsichtiges Parsen und erzwingt Default-Werte.
     */
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }
    }

    /**
     * Konfiguration des [OkHttpClient] mit einem [HttpLoggingInterceptor]
     * zur Protokollierung von HTTP-Anfragen und -Antworten auf Body-Ebene.
     */
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Erzeugung der [TechPulseApi]-Schnittstelle über Retrofit.
     * Verknüpft die Basis-URL, den [OkHttpClient] und den [Json]-Converter-Factory.
     */
    single<TechPulseApi> {
        Retrofit.Builder()
            .baseUrl(TechPulseApi.DEV_TO_BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TechPulseApi::class.java)
    }

    /** Bereitstellung des zentralen [TechPulseRepository] als Singleton. */
    single { TechPulseRepository(get(), get(), get()) }

    // --- ViewModels ---

    /** ViewModel für die Benutzerprofil-Ansicht. */
    viewModelOf(::UserProfileViewModel)

    /** ViewModel für die Beitrags-Detailansicht. */
    viewModelOf(::PostDetailViewModel)

    /** ViewModel für den Artikel-Feed. */
    viewModelOf(::FeedViewModel)

    /** ViewModel für die GitHub-Repository-Suche. */
    viewModelOf(::ReposViewModel)

    /** ViewModel für die Lesezeichen-Übersicht. */
    viewModelOf(::BookmarksViewModel)

    /** ViewModel für die Hauptnavigation und den Anwendungs-State. */
//    viewModel { MainViewModel(repository = get()) }
    viewModelOf(::MainViewModel)

    /** ViewModel für die Einstellungen-Ansicht. */
//    viewModel { SettingsViewModel(repository = get()) }
    viewModelOf(::SettingsViewModel)
}