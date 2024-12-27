package com.example.ratify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.ratify.spotify.Scopes
import com.example.ratify.ui.navigation.MainScreen
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {
    // Keys added in local.properties, accessed in build.gradle
    private val clientId: String by lazy { BuildConfig.SPOTIFY_CLIENT_ID }
    private val redirectUri: String by lazy { BuildConfig.SPOTIFY_REDIRECT_URI }

    private var spotifyAppRemote: SpotifyAppRemote? = null
    private val scopes = arrayOf(Scopes.STREAMING, Scopes.APP_REMOTE_CONTROL, Scopes.USER_READ_PLAYBACK_STATE)
        .map { scope -> scope.value }
        .toTypedArray()

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val response = AuthorizationClient.getResponse(result.resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    connectSpotifyAppRemote()
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("MainActivity", "Auth error: ${response.error}")
                }
                else -> {
                    Log.e("MainActivity", "Auth flow cancelled")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
           MainScreen()
        }
    }

    override fun onStart() {
        super.onStart()

        val builder = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
        builder.setScopes(scopes)
        val request = builder.build()
        val authIntent = AuthorizationClient.createLoginActivityIntent(this, request)
        authLauncher.launch(authIntent)
    }

    private fun connectSpotifyAppRemote() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
            }
        })
    }

    private fun connected() {
        Log.d("MainActivity", "Entered connected")
        spotifyAppRemote?.let { remote ->
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            remote.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            remote.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}