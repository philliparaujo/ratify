package com.example.ratify.mocks

import com.example.ratify.repository.SongRepository
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.ui.theme.RatifyTheme

// Preview device that sets screen rotation to landscape
const val LANDSCAPE_DEVICE = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"

@Composable
fun MyPreview(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    RatifyTheme(
        darkTheme = darkTheme
    ) {
        val fakeSpotifyViewModel = FakeSpotifyViewModel()
        val fakeSongRepository = SongRepository(FakeDao())
        val fakeStateRepository = StateRepository(fakeSongRepository)
        val fakeSettingsRepository = SettingsRepository(LocalContext.current)

        CompositionLocalProvider(
            LocalSpotifyViewModel provides fakeSpotifyViewModel,
            LocalSongRepository provides fakeSongRepository,
            LocalStateRepository provides fakeStateRepository,
            LocalSettingsRepository provides fakeSettingsRepository
        ) {
            content()
        }
    }
}