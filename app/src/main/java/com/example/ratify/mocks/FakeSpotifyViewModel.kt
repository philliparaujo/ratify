package com.example.ratify.mocks

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ratify.settings.ISettingsManager
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.navigation.SnackbarAction
import com.spotify.protocol.types.Capabilities
import com.spotify.protocol.types.PlayerState
import com.spotify.sdk.android.auth.AuthorizationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    override val settings: ISettingsManager
        get() = FakeSettingsManager()

    override fun onEvent(event: SpotifyEvent) {

    }

    override fun showSnackbar(message: String, action: SnackbarAction?) {

    }

    override fun syncPlaybackPositionNow() {

    }
}