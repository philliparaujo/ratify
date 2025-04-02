package com.example.ratify.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface NavigationTarget {
    val title: String
    val icon: ImageVector
}

@Serializable
object MusicNavigationTarget : NavigationTarget {
    override val title = "Music"
    override val icon = Icons.Default.PlayArrow
}

@Serializable
object LibraryNavigationTarget : NavigationTarget {
    override val title = "Library"
    override val icon = Icons.Default.Menu
}

@Serializable
object FavoritesNavigationTarget : NavigationTarget {
    override val title = "Favorites"
    override val icon = Icons.Default.Favorite
}

@Serializable
object SettingsNavigationTarget : NavigationTarget {
    override val title = "Settings"
    override val icon = Icons.Default.Settings
}