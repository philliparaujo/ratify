package com.example.ratify.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface Target {
    val title: String
    val icon: ImageVector
}

@Serializable
object MusicTarget : Target {
    override val title = "Music"
    override val icon = Icons.Default.PlayArrow
}

@Serializable
object LibraryTarget : Target {
    override val title = "Library"
    override val icon = Icons.Default.Menu
}

@Serializable
object SettingsTarget : Target {
    override val title = "Settings"
    override val icon = Icons.Default.Settings
}