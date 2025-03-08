package com.example.ratify.settings

import androidx.compose.ui.graphics.Color
import com.example.ratify.ui.theme.PrimaryCyan
import com.example.ratify.ui.theme.PrimaryGray
import com.example.ratify.ui.theme.PrimaryGreen
import com.example.ratify.ui.theme.PrimaryPurple
import com.example.ratify.ui.theme.PrimaryYellow

enum class PrimaryColor(private val displayName: String, val color: Color) {
    DEFAULT("Default", PrimaryCyan),
    SPOTIFY("Spotify", PrimaryGreen),
    LAVENDER("Lavender", PrimaryPurple),
    GOLDENROD("Goldenrod", PrimaryYellow),
    GRAYSCALE("Grayscale", PrimaryGray);

    override fun toString(): String {
        return displayName
    }
}

