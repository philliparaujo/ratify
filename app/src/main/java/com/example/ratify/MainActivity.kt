package com.example.ratify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.ui.navigation.MainScreen
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {
    private val spotifyViewModel : SpotifyViewModel by viewModels()

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val response = AuthorizationClient.getResponse(result.resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    spotifyViewModel.onEvent(SpotifyEvent.ConnectSpotify)
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

        // Connect to Spotify on successful authentication request
        spotifyViewModel.authRequest.observe(this) { request ->
            val authIntent = AuthorizationClient.createLoginActivityIntent(this, request)
            authLauncher.launch(authIntent)
        }

        // Play a playlist on successful connection
        val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
        spotifyViewModel.spotifyConnectionState.observe(this) { isConnected ->
            if (isConnected == true) {
                Log.d("MainActivity", "Spotify connected successfully!")
                spotifyViewModel.onEvent(SpotifyEvent.PlayPlaylist(playlistURI))
            }  else {
                Log.e("MainActivity", "Failed to connect to Spotify")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing connection before connecting again
        spotifyViewModel.onEvent(SpotifyEvent.DisconnectSpotify)
        
        spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
    }

    override fun onStop() {
        super.onStop()
        spotifyViewModel.onEvent(SpotifyEvent.DisconnectSpotify)
    }
}