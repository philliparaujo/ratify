package com.example.ratify.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface Destination {
    val title: String
    val icon: ImageVector
}

@Serializable
object MusicTarget : Destination {
    override val title = "Music"
    override val icon = Icons.Default.PlayArrow
}

@Serializable
object LibraryTarget : Destination {
    override val title = "Library"
    override val icon = Icons.Default.Menu
}

@Serializable
object SettingsTarget : Destination {
    override val title = "Settings"
    override val icon = Icons.Default.Settings
}