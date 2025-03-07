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

private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = MyCyan,
    onPrimary = MyBlack,
    inversePrimary = MyDarkCyan,

    // Secondary colors
    secondary = MyGray,
    onSecondary = MyWhite,

    // Tertiary colors
    tertiary = MyYellow,
    onTertiary = MyBlack,

    // Background and surface colors
    background = MyBlack,
    onBackground = MyWhite,

    surface = MyBlack,
    surfaceVariant = MyGray,
    onSurfaceVariant = MyBlack,

    primaryContainer = MyDarkGray,
    onPrimaryContainer = MyWhite
)

private val LightColorScheme = darkColorScheme(
    // Primary colors
    primary = MyCyan,
    onPrimary = MyBlack,
    inversePrimary = MyLightCyan,

    // Secondary colors
    secondary = MyGray,
    onSecondary = MyWhite,

    // Tertiary colors
    tertiary = MyYellow,
    onTertiary = MyBlack,

    // Background and surface colors
    background = MyWhite,
    onBackground = MyBlack,

    surface = MyWhite,
    surfaceVariant = MyGray,
    onSurfaceVariant = MyBlack,

    primaryContainer = MyLighterGray,
    onPrimaryContainer = MyBlack
)

@Composable
fun RatifyTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Disabling for now
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

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