package com.example.techpulse.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Repräsentiert ein einzelnes Element in der unteren Navigationsleiste (Bottom Navigation Bar).
 *
 * Kapselt Titel, Icon und das zugehörige Navigationsziel ([Screen]).
 *
 * @property title Der anzuzeigende Beschriftungstext des Navigationselements.
 * @property icon Das zugehörige [ImageVector]-Icon für die UI.
 * @property screen Das verknüpfte Navigationsziel als [Screen].
 */
sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
){

    /** Element für den Haupt-Feed. */
    data object Feed : BottomNavItem(
        "Feed",
        Icons.Default.Home,
        Screen.Feed
    )

    /** Element für die Repository-Suche. */
    data object Repos : BottomNavItem(
        "Repos",
        Icons.Default.Search,
        Screen.Repos
    )

    /** Element für die gespeicherten Lesezeichen. */
    data object Bookmarks : BottomNavItem(
        "Bookmarks",
        Icons.Default.Bookmark,
        Screen.Bookmarks
    )

    /**
     * Eine geordnete Liste aller in der Bottom Navigation Bar anzuzeigenden Elemente.
     */
    companion object{
        val bottomNavItems = listOf(
            Feed,
            Repos,
            Bookmarks
        )
    }
}
