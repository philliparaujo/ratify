package com.example.ratify.di

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.ratify.spotify.ISpotifyViewModel


val LocalSpotifyViewModel = staticCompositionLocalOf<ISpotifyViewModel> {
    error("SpotifyViewModel not provided")
}