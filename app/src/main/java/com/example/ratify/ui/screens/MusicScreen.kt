package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.core.helper.IconButtonSpecs
import com.example.ratify.core.helper.isLandscapeOrientation
import com.example.ratify.core.helper.navigateToSpotifyInstall
import com.example.ratify.core.model.Rating
import com.example.ratify.core.state.MusicState
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.services.updateRatingService
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.components.BinarySetting
import com.example.ratify.ui.components.ImageOverlay
import com.example.ratify.ui.components.Logo
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
    val spotifyConnectionState by spotifyViewModel.remoteConnected.observeAsState()
    val appInstalledState by spotifyViewModel.isSpotifyAppInstalled.observeAsState()

    val connected = spotifyConnectionState == true
    val appInstalled = appInstalledState == true

    if (!appInstalled) {
        AppNotInstalledScreen()
    } else if (!connected) {
        LoginScreen()
    } else {
        PlayerScreen()
    }
}

@Composable
fun AppNotInstalledScreen() {
    val context = LocalContext.current

    val settingsRepository: SettingsRepository = LocalSettingsRepository.current
    val darkTheme = settingsRepository.darkTheme.collectAsState(initial = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Logo at top center
        Logo(
            darkTheme = darkTheme.value,
            primaryColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = if (isLandscapeOrientation()) 32.dp else 64.dp)
        )

        Column(
            modifier = Modifier.padding(top = if (isLandscapeOrientation()) 128.dp else 64.dp).align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            MyButton(
                enabled = true,
                onClick = { navigateToSpotifyInstall(context) },
                text = "Go to Play Store",
                large = true,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_warning_24),
                    contentDescription = "Warning",
                    modifier = Modifier.size(IconButtonSpecs.LARGE.iconSize),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Column(
                    modifier = Modifier.widthIn(max = 500.dp),
                ) {
                    Text(
                        "Spotify is not installed on your device.",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Please install the Spotify app to enable music playback.",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current
    val settingsRepository: SettingsRepository = LocalSettingsRepository.current

    val scope = rememberCoroutineScope()
    val autoSignIn = settingsRepository.autoSignIn.collectAsState(initial = false)
    val darkTheme = settingsRepository.darkTheme.collectAsState(initial = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Logo at top center
        Logo(
            darkTheme = darkTheme.value,
            primaryColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = if (isLandscapeOrientation()) 32.dp else 64.dp)
        )

        // Centered login content
        Column(
            modifier = Modifier.padding(top = if (isLandscapeOrientation()) 128.dp else 64.dp).align(Alignment.Center),
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
                        settingsRepository.setAutoSignIn(newState)
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
    val settingsRepository = LocalSettingsRepository.current

    // Player state
    val userCapabilities = spotifyViewModel.userCapabilities.observeAsState()
    val playerState by spotifyViewModel.playerState.collectAsState()
    val currentPlaybackPosition = spotifyViewModel.currentPlaybackPosition.observeAsState()
    val playerEnabled = userCapabilities.value != null && userCapabilities.value!!.canPlayOnDemand
    val musicState = stateRepository.musicState.collectAsState(initial = MusicState()).value

    // Settings variables
    val skipOnRate = settingsRepository.skipOnRate.collectAsState(initial = false)

    @Composable
    fun RenderSongDisplay() {
        if (playerState != null) {
            SongDisplay(
                title = playerState!!.track.name,
                artists = getArtistsString(playerState!!.track.artists),
                imageUri = spotifyUriToImageUrl(playerState!!.track.imageUri.raw) ?: "",
            )
        } else {
            ImageOverlay(
                imageUri = "",
                renderContent = {
                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = "Play a song on Spotify to get started!",
                            color = MaterialTheme.colorScheme.onSecondary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        }
    }

    @Composable
    fun RenderSongControls() {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val controlsEnabled = playerEnabled && playerState != null

        // Controls playback position dragging
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

        if (controlsEnabled) {
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
            PlaybackPosition(0, 1000, enabled = false)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MyIconButton(
                enabled = controlsEnabled,
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.SkipPrevious)
                },
                icon = ImageVector.vectorResource(id = R.drawable.baseline_skip_previous_24)
            )
            MyIconButton(
                enabled = controlsEnabled,
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
                enabled = controlsEnabled,
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
                if (controlsEnabled) {
                    // Update current rating (UI indicator)
                    val ratingValue = Rating.from(rating)
                    stateRepository.updateCurrentRating(ratingValue)

                    // Update current rating notification service
                    context.updateRatingService(ratingValue)

                    // Update rating in database
                    scope.launch {
                        songRepository.updateRating(
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
            currentRating = musicState.currentRating,
            enabled = controlsEnabled
        )
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        if (isLandscapeOrientation()) {
            // Landscape layout (two columns)

            val totalWidth = maxWidth
            val minRightColumnWidth = 400.dp
            val spacerWidth = 32.dp
            val availableWidth = totalWidth - spacerWidth
            val useEqualWeights = availableWidth >= (minRightColumnWidth * 2)

            if (useEqualWeights) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .fillMaxHeight(),
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
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RenderSongDisplay()
                    }
                    Column(
                        modifier = Modifier
                            .width(minRightColumnWidth)
                            .align(Alignment.CenterVertically),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RenderSongControls()
                    }
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
@PreviewSuite
@Composable
fun NotInstalledScreenPreviews() {
    MyPreview {
        AppNotInstalledScreen()
    }
}

@PreviewSuite
@Composable
fun LoginScreenPreviews() {
    MyPreview {
        LoginScreen()
    }
}

@PreviewSuite
@Composable
fun PlayerScreenPreviews() {
    MyPreview {
        PlayerScreen()
    }
}
