package com.example.ratify.di

import SongRepository
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.StateRepository


val LocalSpotifyViewModel = staticCompositionLocalOf<ISpotifyViewModel> {
    error("SpotifyViewModel not provided")
}

val LocalSongRepository = staticCompositionLocalOf<SongRepository> {
    error("SongRepository not provided")
}

val LocalStateRepository = staticCompositionLocalOf<StateRepository> {
    error("StateRepository not provided")
}