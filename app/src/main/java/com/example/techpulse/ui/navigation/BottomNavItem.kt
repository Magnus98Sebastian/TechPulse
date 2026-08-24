package com.example.techpulse.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
){
    data object Feed : BottomNavItem(
        "Feed",
        Icons.Default.Home,
        Screen.Feed
    )
    data object Repos : BottomNavItem(
        "Repos",
        Icons.Default.Search,
        Screen.Repos
    )
    data object Bookmarks : BottomNavItem(
        "Bookmarks",
        Icons.Default.Bookmark,
        Screen.Bookmarks
    )
    companion object{
        val bottomNavItems = listOf(
            Feed,
            Repos,
            Bookmarks
        )
    }
}
