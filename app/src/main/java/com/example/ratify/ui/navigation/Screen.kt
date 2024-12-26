package com.example.ratify.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface Destination {
    val title: String
    val icon: ImageVector
}

@Serializable
object HomeTarget : Destination {
    override val title = "Home"
    override val icon = Icons.Default.Home
}

@Serializable
object ProfileTarget : Destination {
    override val title = "Profile"
    override val icon = Icons.Default.Person
}

@Serializable
object SettingsTarget : Destination {
    override val title = "Settings"
    override val icon = Icons.Default.Settings
}

@Serializable
object CountManagerTarget : Destination {
    override val title = "Count Manager"
    override val icon = Icons.Default.KeyboardArrowUp
}