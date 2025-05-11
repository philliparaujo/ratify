package com.example.ratify.mocks

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.ui.theme.RatifyTheme

// Preview devices that alter screen rotation, sizing
const val PORTRAIT_DEVICE =
    "spec:width=412dp,height=915dp,dpi=429,isRound=false,chinSize=0dp,orientation=portrait"
const val LANDSCAPE_DEVICE =
    "spec:width=412dp,height=915dp,dpi=429,isRound=false,chinSize=0dp,orientation=landscape"

const val TAB_S6_LITE_PORTRAIT =
    "spec:width=857dp,height=1428dp,dpi=160,isRound=false,chinSize=0dp,orientation=portrait"
const val TAB_S6_LITE_LANDSCAPE =
    "spec:width=857dp,height=1428dp,dpi=160,isRound=false,chinSize=0dp,orientation=landscape"

@Composable
fun MyPreview(
    darkTheme: Boolean = isSystemInDarkTheme(),
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