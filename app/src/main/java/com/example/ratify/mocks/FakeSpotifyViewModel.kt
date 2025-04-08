package com.example.ratify.mocks

import MusicState
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.state.FavoritesState
import com.example.ratify.core.state.LibraryState
import com.example.ratify.database.Song
import com.example.ratify.settings.ISettingsManager
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.navigation.SnackbarAction
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class FakeSpotifyViewModel: ISpotifyViewModel {
    override val authRequest: LiveData<AuthorizationRequest>
        get() = MutableLiveData()
    override val spotifyConnectionState: LiveData<Boolean>
        get() = MutableLiveData(true)
    override val userCapabilities: LiveData<Capabilities>
        get() =  MutableLiveData()
    override val playerState: StateFlow<PlayerState?>
        get() = MutableStateFlow(null)
    override val currentPlaybackPosition: LiveData<Long>
        get() = MutableLiveData()
    override val snackbarHostState: SnackbarHostState
        get() = SnackbarHostState()
    override val musicState: StateFlow<MusicState>
        get() = MutableStateFlow(MusicState())
    override val libraryState: StateFlow<LibraryState>
        get() = MutableStateFlow(LibraryState())
    override val favoritesState: StateFlow<FavoritesState>
        get() = MutableStateFlow(FavoritesState())
    override val settings: ISettingsManager
        get() = FakeSettingsManager()

    override fun onEvent(event: SpotifyEvent) {

    }

    override fun showSnackbar(message: String, action: SnackbarAction?) {

    }

    override fun syncPlaybackPositionNow() {

    }

    override fun getSongsByGroup(
        groupType: GroupType,
        groupName: String,
        uri: String
    ): Flow<List<Song>> {
        return flowOf(emptyList())
    }
}