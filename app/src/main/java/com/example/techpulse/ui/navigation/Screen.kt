package com.example.techpulse.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Feed : Screen
    @Serializable
    data object Bookmarks : Screen
    @Serializable
    data object Repos : Screen
}