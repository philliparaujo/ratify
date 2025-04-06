package com.example.ratify.core.model

import androidx.compose.ui.graphics.Color
import com.example.ratify.ui.theme.PrimaryDarkCyan
import com.example.ratify.ui.theme.PrimaryDarkGray
import com.example.ratify.ui.theme.PrimaryDarkGreen
import com.example.ratify.ui.theme.PrimaryDarkPurple
import com.example.ratify.ui.theme.PrimaryDarkYellow
import com.example.ratify.ui.theme.PrimaryLightCyan
import com.example.ratify.ui.theme.PrimaryLightGray
import com.example.ratify.ui.theme.PrimaryLightGreen
import com.example.ratify.ui.theme.PrimaryLightPurple
import com.example.ratify.ui.theme.PrimaryLightYellow
import com.example.ratify.ui.theme.PrimaryCyan
import com.example.ratify.ui.theme.PrimaryGray
import com.example.ratify.ui.theme.PrimaryGreen
import com.example.ratify.ui.theme.PrimaryPurple
import com.example.ratify.ui.theme.PrimaryYellow

enum class PrimaryColor(
    private val displayName: String,
    val base: Color,
    val lightVariant: Color,
    val darkVariant: Color
) {
    DEFAULT("Default", PrimaryCyan, PrimaryLightCyan, PrimaryDarkCyan),
    SPOTIFY("Spotify", PrimaryGreen, PrimaryLightGreen, PrimaryDarkGreen),
    LAVENDER("Lavender", PrimaryPurple, PrimaryLightPurple, PrimaryDarkPurple),
    GOLDENROD("Goldenrod", PrimaryYellow, PrimaryLightYellow, PrimaryDarkYellow),
    GRAYSCALE("Grayscale", PrimaryGray, PrimaryLightGray, PrimaryDarkGray);

    override fun toString(): String {
        return displayName
    }
}

