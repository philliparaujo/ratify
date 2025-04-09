package com.example.ratify.ui.screens

import MusicState
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.core.model.Rating
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.mocks.LANDSCAPE_DEVICE
import com.example.ratify.mocks.Preview
import com.example.ratify.services.updateRatingService
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.components.BinarySetting
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.components.MyIconButton
import com.example.ratify.ui.components.PlaybackPosition
import com.example.ratify.ui.components.SongDisplay
import com.example.ratify.ui.components.StarRow
import com.example.ratify.ui.components.getArtistsString
import com.example.ratify.ui.components.spotifyUriToImageUrl
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun MusicScreen() {
    val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current
    val spotifyConnectionState by spotifyViewModel.spotifyConnectionState.observeAsState()
    val connected = spotifyConnectionState == true

    if (!connected) {
        LoginScreen()
    } else {
        PlayerScreen()
    }
}

@Composable
fun LoginScreen() {
    val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current

    val settings = spotifyViewModel.settings
    val scope = rememberCoroutineScope()
    val autoSignIn = settings.autoSignIn.collectAsState(initial = false)

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
                    spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
                },
                text = "Connect to Spotify",
                large = true,
            )
            BinarySetting(
                displayText = "Keep me signed in",
                state = autoSignIn.value,
                toggleState = { newState ->
                    scope.launch {
                        settings.setAutoSignIn(newState)
                    }
                }
            )
        }
    }
}

@Composable
fun PlayerScreen() {
    val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current
    val songRepository = LocalSongRepository.current
    val stateRepository = LocalStateRepository.current

    // Player state
    val userCapabilities = spotifyViewModel.userCapabilities.observeAsState()
    val playerState by spotifyViewModel.playerState.collectAsState()
    val currentPlaybackPosition = spotifyViewModel.currentPlaybackPosition.observeAsState()
    val playerEnabled = userCapabilities.value != null && userCapabilities.value!!.canPlayOnDemand
    val musicState = stateRepository.musicState.collectAsState(initial = MusicState()).value

    // Orientation logic
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Settings variables
    val settings = spotifyViewModel.settings
    val skipOnRate = settings.skipOnRate.collectAsState(initial = false)

    @Composable
    fun RenderSongDisplay() {
        if (playerState != null) {
            SongDisplay(
                title = playerState!!.track.name,
                artists = getArtistsString(playerState!!.track.artists),
                imageUri = spotifyUriToImageUrl(playerState!!.track.imageUri.raw) ?: "",
            )
        } else {
            SongDisplay("Placeholder", "Placeholder", "placeholder")
        }
    }

    @Composable
    fun RenderSongControls() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var userDragging by remember { mutableStateOf(false) }
        var dragPositionMs by remember { mutableLongStateOf(0L) }

        LaunchedEffect(userDragging, dragPositionMs, currentPlaybackPosition.value) {
            if (userDragging) {
                val actualPosition = currentPlaybackPosition.value ?: 0L
                if (abs(dragPositionMs - actualPosition) < 1000) {
                    userDragging = false
                }
            }
        }

        if (playerState != null) {
            PlaybackPosition(
                currentPositionMs = if (userDragging) dragPositionMs else {
                    if (playerState!!.isPaused) playerState!!.playbackPosition
                    else currentPlaybackPosition.value
                } ?: 0,
                totalDurationMs = playerState!!.track.duration,
                onValueChanging = {
                    dragPositionMs = it
                    userDragging = true
                },
                onValueChangeFinished = {
                    spotifyViewModel.onEvent(SpotifyEvent.SeekTo(it))
                }
            )
        } else {
            // Placeholder playbackPosition
            PlaybackPosition(0, 1000)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MyIconButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipPrevious)
                },
                icon = ImageVector.vectorResource(id = R.drawable.baseline_skip_previous_24)
            )
            MyIconButton(
                enabled = playerEnabled,
                onClick = {
                    if (playerState != null) {
                        if (playerState!!.isPaused) {
                            spotifyViewModel.onEvent(SpotifyEvent.Resume)
                        } else {
                            spotifyViewModel.onEvent(SpotifyEvent.Pause)
                        }
                    }
                },
                icon = ImageVector.vectorResource(
                    id = if (playerState?.isPaused != false) R.drawable.baseline_play_arrow_24 else R.drawable.baseline_pause_24
                ),
                large = true
            )
            MyIconButton(
                enabled = playerEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
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
                    stateRepository.updateCurrentRating(ratingValue)
//                    spotifyViewModel.onEvent(SpotifyEvent.UpdateCurrentRating(ratingValue))

                    // Update current rating notification service
                    context.updateRatingService(ratingValue)

                    // Update rating in database
                    scope.launch {
                        songRepository.UpdateRating(
                            name = playerState!!.track.name,
                            artists = playerState!!.track.artists,
                            rating = ratingValue,
                            lastRatedTs = System.currentTimeMillis()
                        )
                    }

                    if (skipOnRate.value) {
                        spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
                    }
                }
            },
            currentRating = musicState.currentRating
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
@Preview(name = "Dark Login Screen")
@Composable
fun DarkLoginScreenPreview() {
    Preview(darkTheme = true) {
        LoginScreen()
    }
}

@Preview(name = "Light Login Screen")
@Composable
fun LightLoginScreenPreview() {
    Preview(darkTheme = false) {
        LoginScreen()
    }
}

@Preview(name = "Dark Player Screen")
@Composable
fun DarkPlayerScreenPreview() {
    Preview(darkTheme = true) {
        PlayerScreen()
    }
}

@Preview(name = "Light Player Screen")
@Composable
fun LightPlayerScreenPreview() {
    Preview(darkTheme = false) {
        PlayerScreen()
    }
}

@Preview(
    name = "Dark Landscape Login Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun DarkLandscapeLoginScreenPreview() {
    Preview(darkTheme = true) {
        LoginScreen()
    }
}

@Preview(
    name = "Light Landscape Login Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun LightLandscapeLoginScreenPreview() {
    Preview(darkTheme = false) {
        LoginScreen()
    }
}

@Preview(
    name = "Dark Landscape Player Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun DarkLandscapePlayerScreenPreview() {
    Preview(darkTheme = true) {
        PlayerScreen()
    }
}

@Preview(
    name = "Light Landscape Player Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun LightLandscapePlayerScreenPreview() {
    Preview(darkTheme = false) {
        PlayerScreen()
    }
}
