package com.example.ratify.spotify

import com.example.ratify.repository.SongRepository
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ratify.BuildConfig
import com.example.ratify.database.Converters
import com.example.ratify.services.PLAYER_STATE_SHARED_PREFS
import com.example.ratify.services.TRACK_ARTISTS_SHARED_PREFS
import com.example.ratify.services.TRACK_NAME_SHARED_PREFS
import com.example.ratify.services.updateRatingService
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.ui.navigation.SnackbarAction
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpotifyViewModel(
    application: Application,
    private val songRepository: SongRepository,
    private val stateRepository: StateRepository
): AndroidViewModel(application), ISpotifyViewModel {
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
    override val authRequest = _authRequest

    // Answers "is the user connected?"
    private val _spotifyConnectionState = MutableLiveData<Boolean>()
    override val spotifyConnectionState: LiveData<Boolean> get() = _spotifyConnectionState

    // Provides information on whether the user can play on demand
    private val _userCapabilities = MutableLiveData<Capabilities>()
    override val userCapabilities: LiveData<Capabilities> get() = _userCapabilities
    private var isSubscribedToUserCapabilities = false
    private fun subscribeToUserCapabilities() {
        if (spotifyAppRemote != null && !isSubscribedToUserCapabilities) {
            spotifyAppRemote?.let { remote ->
                remote.userApi.subscribeToCapabilities().setEventCallback {
                    Log.d("SpotifyViewModel", "userCapabilities is $it")
                    _userCapabilities.value = it
                }
            }
            isSubscribedToUserCapabilities = true
        }
    }

    // Provides information on current track, playing/paused, playback position, etc.
    private val _playerState = MutableStateFlow<PlayerState?>(null)
    override val playerState: StateFlow<PlayerState?> get() = _playerState
    private fun subscribeToPlayerState() {
        if (spotifyAppRemote != null) {
            spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
                val previousSong = playerState.value?.track
                val currentSong = state.track

                // Handle song change
                if (previousSong?.uri != currentSong.uri) {
                    Log.d("SpotifyViewModel", "Now playing: ${currentSong.name} by ${currentSong.artist.name}")

                    viewModelScope.launch {
                        val existingSong = songRepository.getSongByPrimaryKey(currentSong)
                        val currentTime = System.currentTimeMillis()

                        val context = getApplication<Application>()
                        val prefs = context.getSharedPreferences(PLAYER_STATE_SHARED_PREFS, Context.MODE_PRIVATE)

                        // In database, insert current song for first time or update existing song's lastPlayedTs, timesPlayed
                        if (existingSong == null) {
                            if (currentSong.name != null && currentSong.artists.isNotEmpty() && currentSong.duration > 0) {
                                songRepository.upsertSong(
                                    track = currentSong,
                                    lastPlayedTs = currentTime,
                                    timesPlayed = 1,
                                    rating = null,
                                    lastRatedTs = null
                                )
                                context.updateRatingService(null)
                            }
                        } else {
                            songRepository.updateLastPlayedTs(
                                name = existingSong.name,
                                artists = existingSong.artists,
                                lastPlayedTs = currentTime,
                                timesPlayed = existingSong.timesPlayed + 1
                            )
                            context.updateRatingService(existingSong.rating)
                        }

                        // Update player state for RatingService
                        val converters = Converters()
                        prefs.edit()
                            .putString(TRACK_NAME_SHARED_PREFS, currentSong.name)
                            .putString(TRACK_ARTISTS_SHARED_PREFS, converters.fromArtistList(currentSong.artists))
                            .apply()

                        // Load current rating based on database entry
                        stateRepository.updateCurrentRating(existingSong?.rating)
                    }
                }

                // Update playerState on state change
                _playerState.value = state

                // Update playback position on play
                if (!state.isPaused) {
                    startUpdatingPlaybackPosition(state.playbackPosition)
                } else {
                    stopUpdatingPlaybackPosition()
                }
            }
        }
    }

    // Provides information on live playback position by incrementing a timer on song being played
    private val _currentPlaybackPosition = MutableLiveData<Long>()
    override val currentPlaybackPosition: LiveData<Long> get() = _currentPlaybackPosition
    private var playbackJob: Job? = null
    private fun startUpdatingPlaybackPosition(initialPosition: Long) {
        stopUpdatingPlaybackPosition() // Stop any existing jobs

        Log.d("SpotifyViewModel", "updating playback position")
        playbackJob = viewModelScope.launch {
            var currentPosition = initialPosition
            val updateIntervalMs = 250L
            val syncIntervalMs = 10000L
            var syncCounter = 0
            while (true) {
                // Frequently update local counter to estimate true playback position
                _currentPlaybackPosition.postValue(currentPosition)
                delay(updateIntervalMs)
                currentPosition += updateIntervalMs

                // Occasionally Sync playback position using Spotify API in a separate coroutine
                if (syncCounter % (syncIntervalMs/updateIntervalMs).toInt() == 0) {
                    launch(Dispatchers.IO) {
                        val syncedPosition = spotifyAppRemote?.playerApi?.playerState?.await()?.data?.playbackPosition
                        if (syncedPosition != null) {
                            currentPosition = syncedPosition
                        }
                    }
                }
                syncCounter++
            }
        }
    }

    private fun stopUpdatingPlaybackPosition() {
        Log.d("SpotifyViewModel", "not updating playback position")
        playbackJob?.cancel()
        playbackJob = null
    }

    override fun syncPlaybackPositionNow() {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeToPlayerState()

            val syncedPosition = spotifyAppRemote?.playerApi?.playerState?.await()?.data?.playbackPosition
            syncedPosition?.let {
                startUpdatingPlaybackPosition(it)
            }
        }
    }

    override fun onEvent(event: SpotifyEvent) {
        when (event) {
            is SpotifyEvent.GenerateAuthorizationRequest -> generateAuthorizationRequest()
            is SpotifyEvent.ConnectSpotify -> connectSpotifyAppRemote()
            is SpotifyEvent.DisconnectSpotify -> disconnectSpotifyAppRemote()

            is SpotifyEvent.PlayPlaylist -> playPlaylist(event.playlistUri)
            is SpotifyEvent.PlaySong -> playSong(event.songUri, event.songName)
            is SpotifyEvent.Pause -> pause()
            is SpotifyEvent.QueueTrack -> queueTrack(event.trackUri, event.trackName)
            is SpotifyEvent.Resume -> resume()
            is SpotifyEvent.SkipNext -> skipNext()
            is SpotifyEvent.SkipPrevious -> skipPrevious()
            is SpotifyEvent.SeekTo -> seekTo(event.positionMs)
            is SpotifyEvent.PlayerEventWhenNotConnected -> playerEventWhenNotConnected()
        }
    }

    // Helper functions for specific events
    private fun generateAuthorizationRequest() {
        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(scopes)
            .build()
        _authRequest.postValue(request)
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
                stateRepository.showSnackbar("Connected to Spotify")
                _spotifyConnectionState.value = true
                subscribeToPlayerState()
                subscribeToUserCapabilities()
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
            _spotifyConnectionState.value = false
            isSubscribedToUserCapabilities = false
        }
        Log.d("SpotifyViewModel", "Disconnected! Yay!")
        stateRepository.showSnackbar("Disconnected from Spotify")
    }

    private fun playPlaylist(playlistURI: String) {
        spotifyAppRemote?.playerApi?.play(playlistURI)
    }

    private fun playSong(songURI: String, songName: String) {
        spotifyAppRemote?.playerApi?.play(songURI)
        stateRepository.showSnackbar("\"${songName}\" now playing")
    }

    private fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    private fun queueTrack(trackURI: String, trackName: String) {
        spotifyAppRemote?.playerApi?.queue(trackURI)
        stateRepository.showSnackbar("\"${trackName}\" added to queue")
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

    private fun seekTo(positionMs: Long) {
        spotifyAppRemote?.playerApi?.seekTo(positionMs)
    }

    private fun playerEventWhenNotConnected() {
        stateRepository.showSnackbar(
            "Not connected to Spotify",
            SnackbarAction(
                name = "Connect",
                action = {
                    onEvent(SpotifyEvent.GenerateAuthorizationRequest)
                }
            )
        )
    }
}