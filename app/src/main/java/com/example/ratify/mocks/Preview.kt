package com.example.ratify.mocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun Preview(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    RatifyTheme(
        darkTheme = darkTheme
    ) {
        CompositionLocalProvider(LocalSpotifyViewModel provides FakeSpotifyViewModel()) {
            content()
        }
    }
}