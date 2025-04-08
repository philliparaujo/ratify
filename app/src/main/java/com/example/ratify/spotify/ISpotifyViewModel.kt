package com.example.ratify.spotify

import MusicState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import com.example.ratify.core.state.FavoritesState
import com.example.ratify.core.state.LibraryState
import com.example.ratify.settings.ISettingsManager
import com.example.ratify.ui.navigation.SnackbarAction
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationRequest
import kotlinx.coroutines.flow.StateFlow

interface ISpotifyViewModel {
    // Saves request to launch authentication, done in MainActivity
    val authRequest: LiveData<AuthorizationRequest>
    // Answers "is the user connected?"
    val spotifyConnectionState: LiveData<Boolean>
    // Provides information on whether the user can play on demand
    val userCapabilities: LiveData<Capabilities>

    // Provides information on current track, playing/paused, playback position, etc.
    val playerState: StateFlow<PlayerState?>
    // Provides information on live playback position by incrementing a timer on song being played
    val currentPlaybackPosition: LiveData<Long>
    // Keeps Snackbars active across any UI changes / screen rotations
    val snackbarHostState: SnackbarHostState

    // Individual screen states
    val musicState: StateFlow<MusicState>
    val libraryState: StateFlow<LibraryState>
    val favoritesState: StateFlow<FavoritesState>

    // Handles management of settings preferences
    val settings: ISettingsManager

    fun onEvent(event: SpotifyEvent)
    fun showSnackbar(message: String, action: SnackbarAction? = null)
    fun syncPlaybackPositionNow()
}