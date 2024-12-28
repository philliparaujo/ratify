package com.example.ratify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    val userCapabilities by spotifyViewModel.userCapabilities.observeAsState()
    val playerState by spotifyViewModel.playerState.observeAsState()

    val connected = spotifyConnectionState == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(if (connected) "Connected" else "Not Connected")

        playerState?.let { state ->
            Text("Now playing: ${state.track.name} by ${state.track.artist.name}")
            Text("Playback state: ${if (state.isPaused) "paused" else "playing"}")
            Text("Playback position: ${state.playbackPosition} / ${state.track.duration}")
        }

        Button(
            enabled = !connected,
            onClick = {
                spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
            }
        ) {
            Text("Connect to Spotify")
        }
        Button(
            enabled = connected,
            onClick = {
                spotifyViewModel.onEvent(SpotifyEvent.GetUserCapabilities)
            }
        ) {
            Text("Print user status")
        }
        Button(
            enabled = connected,
            onClick = {
                // Play a playlist on successful connection
                val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
                if (spotifyConnectionState == true) {
                    Log.d("HomeScreen", "Spotify connected successfully!")
                    spotifyViewModel.onEvent(SpotifyEvent.PlayPlaylist(playlistURI))
                }  else {
                    Log.e("HomeScreen", "Failed to connect to Spotify")
                }
            }
        ) {
            Text("Play indie playlist")
        }
        Row {
            Button(
                enabled = connected,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.Pause)
                }
            ) {
                Text("Pause")
            }
            Button(
                enabled = connected,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.Resume)
                }
            ) {
                Text("Resume")
            }
            Button(
                enabled = connected,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
                }
            ) {
                Text("Next Song")
            }
            Button(
                enabled = connected,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipPrevious)
                }
            ) {
                Text("Previous Song")
            }
        }
    }
}