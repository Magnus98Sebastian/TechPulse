package com.example.techpulse.ui.navigation

import com.example.techpulse.domain.RepositoryItem
import kotlinx.serialization.Serializable

/**
 * Repräsentiert die verschiedenen Navigationsziele (Screens/Routen) innerhalb der Anwendung.
 *
 * Die Typen sind als [Serializable] annotiert, um mit Type-Safe Navigation in Jetpack Compose
 * kompatibel zu sein.
 */
sealed interface Screen {

    /** Hauptansicht des News- und Community-Feeds. */
    @Serializable
    data object Feed : Screen

    /** Übersicht der gespeicherten Lesezeichen (Artikel, Beiträge und Repositories). */
    @Serializable
    data object Bookmarks : Screen

    /** Such- und Übersichtsanzeige für GitHub-Repositories. */
    @Serializable
    data object Repos : Screen

    /**
     * Detailansicht für ein einzelnes GitHub-Repository.
     *
     * @property repoId Die eindeutige ID des darzustellenden Repositories.
     */
    @Serializable
    data class RepoDetailRoute(val repoId: Long) : Screen

    /** Einstellungen und Profil-Konfiguration der Anwendung. */
    @Serializable
    data object Settings : Screen

    /**
     * Detailansicht für einen einzelnen Community-Beitrag inklusive Kommentaren.
     *
     * @property postId Die eindeutige ID des darzustellenden Beitrags.
     */
    @Serializable
    data class PostDetailRoute(val postId: String) : Screen
}