package com.example.ratify.spotify

import androidx.lifecycle.LiveData
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationRequest
import kotlinx.coroutines.flow.StateFlow

interface ISpotifyViewModel {
    // Saves request to launch authentication, done in MainActivity
    val authRequest: LiveData<AuthorizationRequest>
    // Answers if the user has successfully authenticated
    val isAuthenticated: LiveData<Boolean>
    // Answers if the user is connected to Spotify App Remote, used to control song playback
    val remoteConnected: LiveData<Boolean>
    // Provides information on whether the user can play on demand
    val userCapabilities: LiveData<Capabilities>

    // Provides information on current track, playing/paused, playback position, etc.
    val playerState: StateFlow<PlayerState?>
    // Provides information on live playback position by incrementing a timer on song being played
    val currentPlaybackPosition: LiveData<Long>

    fun onEvent(event: SpotifyEvent)
    fun syncPlaybackPositionNow()
}