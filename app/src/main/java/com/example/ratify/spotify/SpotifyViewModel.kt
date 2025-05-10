package com.example.ratify.spotify

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ratify.BuildConfig
import com.example.ratify.core.helper.navigateToSpotifyInstall
import com.example.ratify.core.helper.spotifyPackageName
import com.example.ratify.database.Converters
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.services.PLAYER_STATE_SHARED_PREFS
import com.example.ratify.services.TRACK_ARTISTS_SHARED_PREFS
import com.example.ratify.services.TRACK_NAME_SHARED_PREFS
import com.example.ratify.services.updateRatingService
import com.example.ratify.ui.navigation.SnackbarAction
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Manages Spotify permissions, authorization/connection state, playback state, and actions
class SpotifyViewModel(
    application: Application,
    private val songRepository: SongRepository,
    private val stateRepository: StateRepository
): AndroidViewModel(application), ISpotifyViewModel {
    // Keys added in local.properties, accessed in build.gradle
    private val clientId: String by lazy { BuildConfig.SPOTIFY_CLIENT_ID }
    private val redirectUri: String by lazy { BuildConfig.SPOTIFY_REDIRECT_URI }

    // Permissions given to users of app, ideally minimized as much as possible
    private val scopes = arrayOf(
        Scopes.STREAMING,
        Scopes.APP_REMOTE_CONTROL,
        Scopes.USER_READ_PLAYBACK_STATE
    )
        .map { scope -> scope.value }
        .toTypedArray()

    // Allows access to Spotify Remote API
    private var spotifyAppRemote: SpotifyAppRemote? = null

    // Saves request to launch authentication, done in MainActivity
    private val _authRequest = MutableLiveData<AuthorizationRequest>()
    override val authRequest = _authRequest

    // Answers if the user has Spotify installed on device
    private val _isSpotifyAppInstalled = MutableLiveData<Boolean>()
    override val isSpotifyAppInstalled: LiveData<Boolean> get() = _isSpotifyAppInstalled
    override fun setSpotifyAppInstalled(installed: Boolean) {
        _isSpotifyAppInstalled.value = installed
    }
    override fun isSpotifyAppInstalled(context: Context): Boolean {
        return context.packageManager.getLaunchIntentForPackage(spotifyPackageName) != null
    }

    // Answers if the user is connected to Spotify App Remote, used to control song playback
    private val _remoteConnected = MutableLiveData<Boolean>()
    override val remoteConnected: LiveData<Boolean> get() = _remoteConnected

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
        val remote = spotifyAppRemote ?: return

        remote.playerApi.subscribeToPlayerState().setEventCallback { newPlayerState ->
            val previousSong = playerState.value?.track
            val currentSong = newPlayerState.track

            // On song change, update current rating, RatingService, and database updates
            if (previousSong?.uri != currentSong.uri) {
                Log.d("SpotifyViewModel", "Now playing: ${currentSong.name} by ${currentSong.artist.name}")

                viewModelScope.launch {
                    handleSongChange(currentSong)
                }
            }

            // Update playback position on play
            if (!newPlayerState.isPaused) {
                startUpdatingPlaybackPosition(newPlayerState.playbackPosition)
            } else {
                stopUpdatingPlaybackPosition()
            }

            // Update playerState
            _playerState.value = newPlayerState
        }
    }
    private suspend fun handleSongChange(currentSong: Track) {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences(PLAYER_STATE_SHARED_PREFS, Context.MODE_PRIVATE)

        val currentSongInDb = songRepository.getSongByPrimaryKey(currentSong)
        val currentTime = System.currentTimeMillis()

        // In database, insert current song for first time or update existing song's lastPlayedTs, timesPlayed
        if (currentSongInDb == null) {
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
                name = currentSongInDb.name,
                artists = currentSongInDb.artists,
                lastPlayedTs = currentTime,
                timesPlayed = currentSongInDb.timesPlayed + 1
            )
            context.updateRatingService(currentSongInDb.rating)
        }

        // Update player state for RatingService
        val converters = Converters()
        prefs.edit()
            .putString(TRACK_NAME_SHARED_PREFS, currentSong.name)
            .putString(TRACK_ARTISTS_SHARED_PREFS, converters.fromArtistList(currentSong.artists))
            .apply()

        // Load current rating based on database entry
        stateRepository.updateCurrentRating(currentSongInDb?.rating)
    }

    // Provides information on live playback position by incrementing a timer on song being played
    private val _currentPlaybackPosition = MutableLiveData<Long>()
    override val currentPlaybackPosition: LiveData<Long> get() = _currentPlaybackPosition
    private var playbackJob: Job? = null
    private fun startUpdatingPlaybackPosition(initialPosition: Long) {
        stopUpdatingPlaybackPosition() // Stop any existing jobs

        val syncIntervalMs = 10000L
        val updateIntervalMs = 250L
        val syncRate = (syncIntervalMs/updateIntervalMs).toInt()

        Log.d("SpotifyViewModel", "updating playback position")
        playbackJob = viewModelScope.launch {
            var currentPosition = initialPosition
            var syncCounter = 0

            while (true) {
                // Frequently update local counter to estimate true playback position
                _currentPlaybackPosition.postValue(currentPosition)
                delay(updateIntervalMs)
                currentPosition += updateIntervalMs

                // Occasionally sync playback position using Spotify API in a separate coroutine
                if (syncCounter % syncRate == 0) {
                    launch(Dispatchers.IO) {
                        fetchPlaybackPosition()?.let { currentPosition = it }
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

    private fun fetchPlaybackPosition(): Long? {
        return spotifyAppRemote?.playerApi?.playerState?.await()?.data?.playbackPosition
    }

    override fun syncPlaybackPositionNow() {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeToPlayerState()
            fetchPlaybackPosition()?.let { startUpdatingPlaybackPosition(it) }
        }
    }

    override fun onEvent(event: SpotifyEvent) {
        when (event) {
            is SpotifyEvent.GenerateAuthorizationRequest -> generateAuthorizationRequest()
            is SpotifyEvent.ConnectAppRemote -> connectSpotifyAppRemote()
            is SpotifyEvent.DisconnectAppRemote -> disconnectSpotifyAppRemote()

            is SpotifyEvent.PlayPlaylist -> playPlaylist(event.playlistUri)
            is SpotifyEvent.PlaySong -> playSong(event.songUri, event.songName, event.queueSkip)
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
        // If Spotify not installed on phone, cannot connect to App Remote
        val isInstalled = _isSpotifyAppInstalled.value == true
        if (!isInstalled) {
            Log.d("SpotifyViewModel", "Spotify app not installed, skipping App Remote connection")
            return
        }

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(getApplication(), connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("SpotifyViewModel", "Connected! Yay!")
                stateRepository.showSnackbar("Connected to Spotify")
                _remoteConnected.value = true
                subscribeToPlayerState()
                subscribeToUserCapabilities()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyViewModel", throwable.message, throwable)
                _remoteConnected.value = false
            }
        })
    }

    private fun disconnectSpotifyAppRemote() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
            _remoteConnected.value = false
            isSubscribedToUserCapabilities = false
        }
        Log.d("SpotifyViewModel", "Disconnected! Yay!")
        stateRepository.showSnackbar("Disconnected from Spotify")
    }

    private fun playPlaylist(playlistURI: String) {
        spotifyAppRemote?.playerApi?.play(playlistURI)
    }

    private fun playSong(songURI: String, songName: String, queueSkip: Boolean) {
        viewModelScope.launch {
            if (queueSkip) {
                queueTrack(songURI, songName)
                Thread.sleep(1000)
                skipNext()
            } else {
                spotifyAppRemote?.playerApi?.play(songURI)
                stateRepository.showSnackbar("\"$songName\" now playing")
            }
        }
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
        val context = getApplication<Application>()
        val isInstalled = _isSpotifyAppInstalled.value == true

        if (isInstalled) {
            stateRepository.showSnackbar(
                "Not connected to Spotify",
                SnackbarAction(
                    name = "Connect",
                    action = {
                        onEvent(SpotifyEvent.GenerateAuthorizationRequest)
                    }
                )
            )
        } else {
            stateRepository.showSnackbar(
                "Spotify is not installed",
                SnackbarAction(
                    name = "Install",
                    action = {
                        navigateToSpotifyInstall(context)
                    }
                )
            )
        }
    }
}