package com.example.ratify.di

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.spotify.ISpotifyViewModel

val LocalSpotifyViewModel = staticCompositionLocalOf<ISpotifyViewModel> {
    error("SpotifyViewModel not provided")
}

val LocalSongRepository = staticCompositionLocalOf<SongRepository> {
    error("SongRepository not provided")
}

val LocalStateRepository = staticCompositionLocalOf<StateRepository> {
    error("StateRepository not provided")
}

val LocalSettingsRepository = staticCompositionLocalOf<SettingsRepository> {
    error("SettingsRepository not provided")
}