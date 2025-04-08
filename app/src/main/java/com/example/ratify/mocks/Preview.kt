package com.example.ratify.mocks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.ui.theme.RatifyTheme

// Preview device that sets screen rotation to landscape
const val LANDSCAPE_DEVICE = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"

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