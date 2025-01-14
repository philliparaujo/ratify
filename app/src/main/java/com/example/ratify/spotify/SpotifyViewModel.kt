package com.example.ratify.spotify

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ratify.BuildConfig
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.SearchType
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SongDao
import com.example.ratify.spotifydatabase.SongState
import com.example.ratify.spotifydatabase.SortType
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SpotifyViewModel(
    application: Application,
    private val dao: SongDao
): AndroidViewModel(application) {
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

    // Provides information on whether the user can play on demand
    private val _userCapabilities = MutableLiveData<Capabilities>()
    val userCapabilities: LiveData<Capabilities> get() = _userCapabilities
    private var isSubscribedToUserCapabilities = false
    private fun subscribeToUserCapabilities() {
        if (spotifyAppRemote != null && !isSubscribedToUserCapabilities) {
            spotifyAppRemote?.let { remote ->
                remote.userApi.subscribeToCapabilities().setEventCallback {
                    Log.d("SpotifyViewModel", "userCapabilities is " + it)
                    _userCapabilities.value = it
                }
            }
            isSubscribedToUserCapabilities = true
        }
    }

    // Provides information on current track, playing/paused, playback position, etc.
    private val _playerState = MutableLiveData<PlayerState?>()
    val playerState: LiveData<PlayerState?> get() = _playerState
    private var isSubscribedToPlayerState = false
    private fun subscribeToPlayerState() {
        if (spotifyAppRemote != null && !isSubscribedToPlayerState) {
            spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback { state ->
                val previousSong = playerState.value?.track
                val currentSong = state.track

                // Handle song change
                if (previousSong?.uri != currentSong.uri) {
                    Log.d("SpotifyViewModel", "Now playing: ${currentSong.name} by ${currentSong.artist.name}")

                    viewModelScope.launch {
                        val existingSong = dao.getSongByUri(currentSong.uri) // Query database
                        val currentTime = System.currentTimeMillis()

                        // Insert current song for first time or simply update its lastPlayedTs
                        if (existingSong == null) {
                            onEvent(SpotifyEvent.UpsertSong(
                                track = currentSong,
                                lastPlayedTs = currentTime,
                                timesPlayed = 1,
                                rating = null,
                                lastRatedTs = null
                            ))
                        } else {
                            onEvent(SpotifyEvent.UpdateLastPlayedTs(
                                uri = currentSong.uri,
                                lastPlayedTs = currentTime,
                                timesPlayed = existingSong.timesPlayed + 1
                            ))
                        }

                        // Load current rating based on database entry
                        _rating.value = existingSong?.rating
                    }
                }

                // Update playerState on state change
                _playerState.postValue(state)

                // Update playback position on play
                if (!state.isPaused) {
                    startUpdatingPlaybackPosition(state.playbackPosition)
                } else {
                    stopUpdatingPlaybackPosition()
                }
            }
            isSubscribedToPlayerState = true
        }
    }

    // Provides information on live playback position by incrementing a timer on song being played
    private val _currentPlaybackPosition = MutableLiveData<Long>()
    val currentPlaybackPosition: LiveData<Long> get() = _currentPlaybackPosition
    private var playbackJob: Job? = null
    private fun startUpdatingPlaybackPosition(initialPosition: Long) {
        // Stop any existing jobs
        stopUpdatingPlaybackPosition()

        Log.d("SpotifyViewModel", "updating playback position")
        playbackJob = viewModelScope.launch {
            var currentPosition = initialPosition
            while (true) {
                _currentPlaybackPosition.postValue(currentPosition)
                delay(1000L)
                currentPosition += 1000L
            }
        }
    }
    private fun stopUpdatingPlaybackPosition() {
        Log.d("SpotifyViewModel", "not updating playback position")
        playbackJob?.cancel()
        playbackJob = null
    }

    // Database variables
    private val _searchType = MutableStateFlow(SearchType.NAME)
    private val _searchQuery = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _sortType = MutableStateFlow(SortType.LAST_PLAYED_TS)
    private val _rating = MutableStateFlow<Rating?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
//    private val _songs = combine(_sortType, _searchType, _searchQuery) { sortType, searchType, query ->
//        when {
//            query.isNotEmpty() -> when (searchType) {
//                SearchType.NAME -> dao.searchByName(query)
//                SearchType.ARTISTS -> dao.searchByArtistName(query)
//                SearchType.RATING -> dao.searchByRating(query.toIntOrNull() ?: -1)
//            }
//            else -> when (sortType) {
//                SortType.LAST_PLAYED_TS -> dao.getSongsOrderedByLastPlayedTs()
//                SortType.LAST_RATED_TS -> dao.getSongsOrderedByLastRatedTs()
//                SortType.RATING -> dao.getSongsOrderedByRating()
//            }
//        }
//    }.flatMapLatest { it }
    private val _songs = combine(_searchType, _searchQuery, _sortType) { searchType, searchQuery, sortType ->
        dao.querySongs(dao.buildQuery(searchType, searchQuery, sortType))
    }.flatMapLatest { it }

    private val _state = MutableStateFlow(SongState())
    val state = combine(
        listOf(_state, _searchType, _sortType, _rating, _songs, _searchQuery, _isSearching)
    ) { flows: Array<Any?> ->
        val state = flows[0] as SongState
        val searchType = flows[1] as SearchType
        val sortType = flows[2] as SortType
        val rating = flows[3] as Rating?
        val songs = flows[4] as List<Song>
        val searchQuery = flows[5] as String
        val isSearching = flows[6] as Boolean

        state.copy(
            songs = songs,
            searchType = searchType,
            sortType = sortType,
            currentRating = rating,
            searchQuery = searchQuery,
            isSearching = isSearching
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SongState())

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
    }

    fun onEvent(event: SpotifyEvent) {
        when (event) {
            is SpotifyEvent.GenerateAuthorizationRequest -> generateAuthorizationRequest()
            is SpotifyEvent.ConnectSpotify -> connectSpotifyAppRemote()
            is SpotifyEvent.DisconnectSpotify -> disconnectSpotifyAppRemote()
            is SpotifyEvent.PlayPlaylist -> playPlaylist(event.playlistUri)
            is SpotifyEvent.Pause -> pause()
            is SpotifyEvent.Resume -> resume()
            is SpotifyEvent.SkipNext -> skipNext()
            is SpotifyEvent.SkipPrevious -> skipPrevious()

            is SpotifyEvent.DeleteSong -> {
                deleteSong(event.song)
            }
            is SpotifyEvent.DeleteSongsWithNullRating -> {
                deleteSongWithNullRating(event.exceptUri)
            }
            is SpotifyEvent.UpdateSearchType -> {
                _searchType.value = event.searchType
            }
            is SpotifyEvent.UpdateSortType -> {
                _sortType.value = event.sortType
            }
            is SpotifyEvent.UpdateCurrentRating -> {
                _rating.value = event.rating
            }
            is SpotifyEvent.UpdateLastPlayedTs -> {
                updateLastPlayedTs(event.uri, event.lastPlayedTs)
            }
            is SpotifyEvent.UpdateRating -> {
                updateRating(event.uri, event.rating, event.lastRatedTs)
            }
            is SpotifyEvent.UpsertSong -> {
                upsertSong(
                    Song(
                        album = event.track.album,
                        artist = event.track.artist,
                        artists = event.track.artists,
                        duration = event.track.duration,
                        imageUri = event.track.imageUri,
                        name = event.track.name,
                        uri = event.track.uri,
                        lastPlayedTs = event.lastPlayedTs,
                        timesPlayed = event.timesPlayed,
                        lastRatedTs = event.lastRatedTs,
                        rating = event.rating,
                    )
                )
            }
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
            isSubscribedToPlayerState = false
            isSubscribedToUserCapabilities = false
        }
        Log.d("SpotifyViewModel", "Disconnected! Yay!")
    }

    private fun playPlaylist(playlistURI: String) {
        spotifyAppRemote?.playerApi?.play(playlistURI)
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

    // Database functions
    private fun upsertSong(song: Song) {
        viewModelScope.launch {
            dao.upsertSong(song)
        }
    }

    private fun deleteSong(song: Song) {
        viewModelScope.launch {
            dao.deleteSong(song)
        }
    }

    private fun deleteSongWithNullRating(exceptUri: String) {
        viewModelScope.launch {
            dao.deleteSongsWithNullRating(exceptUri)
        }
    }

    private fun updateLastPlayedTs(uri: String, lastPlayedTs: Long?) {
        viewModelScope.launch {
            val song = dao.getSongByUri(uri)
            if (song != null) {
                dao.upsertSong(song.copy(lastPlayedTs = lastPlayedTs))
            }
        }
    }

    private fun updateRating(uri: String, rating: Rating?, lastRatedTs: Long?) {
        viewModelScope.launch {
            val song = dao.getSongByUri(uri)
            if (song != null) {
                dao.upsertSong(song.copy(lastRatedTs = lastRatedTs, rating = rating))
            }
        }
    }
}