package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.SongState
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.components.MyIconButton
import com.example.ratify.ui.components.PlaybackPosition
import com.example.ratify.ui.components.SongDisplay
import com.example.ratify.ui.components.StarRow
import com.example.ratify.ui.components.getArtistsString
import com.example.ratify.ui.components.spotifyUriToImageUrl
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun MusicScreen(
    spotifyViewModel: SpotifyViewModel,
) {
    val spotifyConnectionState by spotifyViewModel.spotifyConnectionState.observeAsState()
    val connected = spotifyConnectionState == true

    if (!connected) {
        LoginScreen(
            spotifyViewModel
        )
    } else {
        PlayerScreen(
            spotifyViewModel
        )
    }
}

@Composable
fun LoginScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MyButton(
                enabled = true,
                onClick = {
                    spotifyViewModel?.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
                },
                text = "Connect to Spotify",
                large = true,
            )
            Text(
                text = "Keep me signed in",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun PlayerScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    val userCapabilities = spotifyViewModel?.userCapabilities?.observeAsState()
    val playerState = spotifyViewModel?.playerState?.observeAsState()
    val currentPlaybackPosition = spotifyViewModel?.currentPlaybackPosition?.observeAsState()
    val playerEnabled = userCapabilities?.value != null && userCapabilities.value!!.canPlayOnDemand
    val songState = spotifyViewModel?.state?.collectAsState(initial = SongState())

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    @Composable
    fun RenderSongDisplay() {
        if (playerState?.value != null) {
            SongDisplay(
                title = playerState.value!!.track.name,
                artists = getArtistsString(playerState.value!!.track.artists),
                imageUri = spotifyUriToImageUrl(playerState.value!!.track.imageUri.raw) ?: "",
            )
        } else {
            SongDisplay("Placeholder", "Placeholder", "placeholder")
        }
    }

    @Composable
    fun RenderSongControls() {
        if (playerState?.value != null) {
            PlaybackPosition(
                currentPositionMs = (if (playerState.value!!.isPaused) playerState.value!!.playbackPosition else currentPlaybackPosition?.value) ?: 0,
                totalDurationMs = playerState.value!!.track.duration,
            )
        } else {
            PlaybackPosition(0, 1000)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MyIconButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel?.onEvent(SpotifyEvent.SkipPrevious)
                },
                icon = ImageVector.vectorResource(id = R.drawable.baseline_skip_previous_24)
            )
            MyIconButton(
                enabled = playerEnabled,
                onClick = {
                    if (playerState != null) {
                        if (playerState.value!!.isPaused) {
                            spotifyViewModel.onEvent(SpotifyEvent.Resume)
                        } else {
                            spotifyViewModel.onEvent(SpotifyEvent.Pause)
                        }
                    }
                },
                icon = ImageVector.vectorResource(
                    id = if (playerState?.value?.isPaused != false) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24
                ),
                large = true
            )
            MyIconButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel?.onEvent(SpotifyEvent.SkipNext)
                },
                icon = ImageVector.vectorResource(id = R.drawable.baseline_skip_next_24)
            )
        }
        StarRow(
            scale = 1f,
            starCount = 5,
            onRatingSelect = { rating ->
                if (playerEnabled) {
                    // Update current rating (UI indicator)
                    val ratingValue = Rating.from(rating)
                    spotifyViewModel?.onEvent(SpotifyEvent.UpdateCurrentRating(ratingValue))

                    // Update rating in database
                    spotifyViewModel?.onEvent(SpotifyEvent.UpdateRating(
                        uri = playerState?.value!!.track.uri,
                        rating = ratingValue,
                        lastRatedTs = System.currentTimeMillis()
                    ))
                }
            },
            currentRating = songState?.value?.currentRating
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        if (isLandscape) {
            // Landscape layout (two columns)
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RenderSongDisplay()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RenderSongControls()
                }
            }
        } else {
            // Portrait layout (one column)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RenderSongDisplay()
                RenderSongControls()
            }
        }
    }
}

// Previews
@Preview(name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    RatifyTheme {
        LoginScreen(
            spotifyViewModel = null
        )
    }
}

@Preview(name = "Player Screen")
@Composable
fun PlayerScreenPreview() {
    RatifyTheme {
        PlayerScreen(
            spotifyViewModel = null
        )
    }
}

public const val landscapeDevice = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
@Preview(
    name = "Landscape Login Screen",
    device = landscapeDevice  // A hack to force landscape render of previews
)
@Composable
fun LandscapeLoginScreenPreview() {
    RatifyTheme {
        LoginScreen(
            spotifyViewModel = null
        )
    }
}

@Preview(
    name = "Landscape Player Screen",
    device = landscapeDevice
)
@Composable
fun LandscapePlayerScreenPreview() {
    RatifyTheme {
        PlayerScreen(
            spotifyViewModel = null
        )
    }
}