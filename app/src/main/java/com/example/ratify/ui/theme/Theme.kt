package com.example.ratify.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ratify.core.model.PrimaryColor

private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryCyan,
    onPrimary = Black,
    inversePrimary = PrimaryDarkCyan,

    // Secondary colors
    secondary = Gray,
    onSecondary = White,

    // Tertiary colors
    tertiary = Yellow,
    onTertiary = Black,

    // Background and surface colors
    background = Black,
    onBackground = White,

    surface = Black,
    surfaceVariant = Gray,
    onSurfaceVariant = Black,

    primaryContainer = DarkGray,
    onPrimaryContainer = White
)

private val LightColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryCyan,
    onPrimary = Black,
    inversePrimary = PrimaryLightCyan,

    // Secondary colors
    secondary = Gray,
    onSecondary = White,

    // Tertiary colors
    tertiary = Yellow,
    onTertiary = Black,

    // Background and surface colors
    background = White,
    onBackground = Black,

    surface = White,
    surfaceVariant = Gray,
    onSurfaceVariant = Black,

    primaryContainer = LightGray,
    onPrimaryContainer = Black
)

@Composable
fun RatifyTheme(
    darkTheme: Boolean,
    themeColor: Int = PrimaryColor.DEFAULT.ordinal,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Disabling for now
    content: @Composable () -> Unit
) {
    // Get proper light/dark theme
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Get primary color values
    val primaryColorOption = PrimaryColor.entries.toTypedArray()[themeColor]
    val primaryColor = primaryColorOption.base
    val inversePrimaryColor = if (darkTheme) {
        primaryColorOption.darkVariant
    } else {
        primaryColorOption.lightVariant
    }

    val colorScheme = baseColorScheme.copy(
        primary = primaryColor,
        inversePrimary = inversePrimaryColor
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // Hack to force different theme preview backgrounds, real app works without this
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            content()
        }
    }
}