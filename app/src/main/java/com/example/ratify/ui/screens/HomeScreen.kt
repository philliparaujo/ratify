package com.example.ratify.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.SongState
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.components.StarRow

@Composable
fun HomeScreen(
    spotifyViewModel: SpotifyViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val spotifyConnectionState by spotifyViewModel.spotifyConnectionState.observeAsState()
    val userCapabilities by spotifyViewModel.userCapabilities.observeAsState()
    val playerState by spotifyViewModel.playerState.observeAsState()
    val currentPlaybackPosition by spotifyViewModel.currentPlaybackPosition.observeAsState()

    val connected = spotifyConnectionState == true
    val canPlayOnDemand = userCapabilities != null && userCapabilities!!.canPlayOnDemand
    val playerEnabled = connected && canPlayOnDemand

    val songState by spotifyViewModel.state.collectAsState(initial = SongState())

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
            Text("Playback position: ${if (state.isPaused) state.playbackPosition else currentPlaybackPosition} / ${state.track.duration}")
        }

        MyButton(
            enabled = !connected,
            onClick = {
                spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
            },
            text = "Connect to Spotify"
        )
        MyButton(
            enabled = playerEnabled,
            onClick = {
                // Play a playlist on successful connection
                val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
                if (spotifyConnectionState == true) {
                    Log.d("HomeScreen", "Spotify connected successfully!")
                    spotifyViewModel.onEvent(SpotifyEvent.PlayPlaylist(playlistURI))
                }  else {
                    Log.e("HomeScreen", "Failed to connect to Spotify")
                }
            },
            text = "Play indie playlist"
        )
        Row {
            MyButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.Pause)
                },
                text = "Pause"
            )
            MyButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.Resume)
                },
                text = "Resume"
            )
            MyButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
                },
                text = "Next Song"
            )
            MyButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipPrevious)
                },
                text = "Previous song"
            )
        }
        Text("${songState.currentRating?.value}")
        StarRow(
            scale = 1f,
            starCount = 5,
            onRatingSelect = { rating ->
                // Update current rating (UI indicator)
                val ratingValue = Rating.from(rating)
                spotifyViewModel.onEvent(SpotifyEvent.UpdateCurrentRating(ratingValue))

                // Update rating in database
                spotifyViewModel.onEvent(SpotifyEvent.UpdateRating(
                    uri = playerState!!.track.uri,
                    rating = ratingValue,
                    lastRatedTs = System.currentTimeMillis()
                ))
            },
            currentRating = songState.currentRating
        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//        ) {
//            for (i in 1..10) {
//                MyButton(
//                    enabled = songState.currentRating?.value != i,
//                    onClick = {
//                        // Update current rating (UI indicator)
//                        val newRating = Rating.from(i)
//                        spotifyViewModel.onEvent(SpotifyEvent.UpdateCurrentRating(newRating))
//
//                        // Update rating in database
//                        spotifyViewModel.onEvent(SpotifyEvent.UpdateRating(
//                            uri = playerState!!.track.uri,
//                            rating = newRating,
//                            lastRatedTs = System.currentTimeMillis()
//                        ))
//                    },
//                    modifier = Modifier
//                        .weight(1f),
//                    text = ""
//                )
//            }
//        }

        Row {
            MyButton(
                onClick = {
                    onExportClick.invoke()
                },
                text = "Export Database"
            )
            MyButton(
                onClick = {
                    onImportClick.invoke()
                },
                text = "Import Database"
            )
        }
    }
}