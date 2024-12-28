package com.example.ratify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel

@Composable
fun HomeScreen(
    spotifyViewModel: SpotifyViewModel
) {
    val spotifyConnectionState by spotifyViewModel.spotifyConnectionState.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = {
            spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
        }) {
            Text("Connect to Spotify")
        }
        Button(onClick = {
            // Play a playlist on successful connection
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            if (spotifyConnectionState == true) {
                Log.d("HomeScreen", "Spotify connected successfully!")
                spotifyViewModel.onEvent(SpotifyEvent.PlayPlaylist(playlistURI))
            }  else {
                Log.e("HomeScreen", "Failed to connect to Spotify")
            }
        }) {
            Text("Play indie playlist")
        }
    }
}