package com.example.ratify.spotify

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratify.BuildConfig
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyViewModel(application: Application): AndroidViewModel(application) {
    // Keys added in local.properties, accessed in build.gradle
    private val clientId: String by lazy { BuildConfig.SPOTIFY_CLIENT_ID }
    private val redirectUri: String by lazy { BuildConfig.SPOTIFY_REDIRECT_URI }

    // Permissions given to users of app, ideally minimized as much as possible
    private val scopes = arrayOf(Scopes.STREAMING, Scopes.APP_REMOTE_CONTROL, Scopes.USER_READ_PLAYBACK_STATE)
        .map { scope -> scope.value }
        .toTypedArray()

    // Allows access to Spotify Remote API
    private var spotifyAppRemote: SpotifyAppRemote? = null

    // Saves request to launch authentication, done in MainActivity
    private val _authRequest = MutableLiveData<AuthorizationRequest>()
    val authRequest = _authRequest

    // Answers "is the user connected?"
    private val _spotifyConnectionState = MutableLiveData<Boolean>()
    val spotifyConnectionState: LiveData<Boolean> get() = _spotifyConnectionState

    private val _userCapabilities = MutableLiveData<Capabilities>()
    val userCapabilities: LiveData<Capabilities> get() = _userCapabilities

    // Provides information on current track, playing/paused, playback position, etc.
    private val _playerState = MutableLiveData<PlayerState?>()
    val playerState: LiveData<PlayerState?> get() = _playerState

    fun onEvent(event: SpotifyEvent) {
        when (event) {
            is SpotifyEvent.GenerateAuthorizationRequest -> generateAuthorizationRequest()
            is SpotifyEvent.ConnectSpotify -> connectSpotifyAppRemote()
            is SpotifyEvent.DisconnectSpotify -> disconnectSpotifyAppRemote()
            is SpotifyEvent.PlayPlaylist -> playPlaylist(event.playlistUri)
            is SpotifyEvent.GetUserCapabilities -> printUserStatus()
            is SpotifyEvent.Pause -> pause()
            is SpotifyEvent.Resume -> resume()
            is SpotifyEvent.SkipNext -> skipNext()
            is SpotifyEvent.SkipPrevious -> skipPrevious()
        }
    }

    private fun generateAuthorizationRequest() {
        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(scopes)
            .build()
        _authRequest.value = request
    }

    private fun connectSpotifyAppRemote() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(getApplication(), connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("SpotifyViewModel", "Connected! Yay!")
                _spotifyConnectionState.value = true
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyViewModel", throwable.message, throwable)
                _spotifyConnectionState.value = false
            }
        })
    }

    private fun disconnectSpotifyAppRemote() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
        }
        Log.d("SpotifyViewModel", "Disconnected! Yay!")
    }

    private fun playPlaylist(playlistURI: String) {
        spotifyAppRemote?.let { remote ->
            // Play a playlist
            remote.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            remote.playerApi.subscribeToPlayerState().setEventCallback {
                _playerState.value = it
                val track: Track = it.track
                Log.d("SpotifyViewModel", track.name + " by " + track.artist.name)
            }
        }
    }

    private fun printUserStatus() {
        spotifyAppRemote?.let { remote ->
            remote.userApi.subscribeToCapabilities().setEventCallback {
                Log.d("SpotifyViewModel", "userCapabilities is " + it)
                _userCapabilities.value = it
            }
        }
    }

    private fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    private fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    private fun skipNext() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    private fun skipPrevious() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }
}