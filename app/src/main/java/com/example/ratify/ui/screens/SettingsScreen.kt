package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.services.PlaylistFilterService
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.PlaylistCreationState
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.components.BinarySetting
import com.example.ratify.ui.components.CreatePlaylistDialog
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.components.ThemeSelector
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    spotifyViewModel: ISpotifyViewModel? = null,
    playlistFilterService: PlaylistFilterService? = null
) {
    val settingsRepository: SettingsRepository = LocalSettingsRepository.current

    val scope = rememberCoroutineScope()

    // Playlist creation state
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successPlaylistName by remember { mutableStateOf("") }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val playlistCreationState by spotifyViewModel?.playlistCreationState?.observeAsState() ?: remember { mutableStateOf(PlaylistCreationState.Idle) }

    // Handle playlist creation state changes
    LaunchedEffect(playlistCreationState) {
        when (val state = playlistCreationState) {
            is PlaylistCreationState.Loading -> {
                showLoadingDialog = true
                showPlaylistDialog = false
            }
            is PlaylistCreationState.Success -> {
                showLoadingDialog = false
                successPlaylistName = state.playlistName
                showSuccessDialog = true
            }
            is PlaylistCreationState.Error -> {
                showLoadingDialog = false
                errorMessage = state.message
                showErrorDialog = true
            }
            is PlaylistCreationState.Idle -> {
                showLoadingDialog = false
            }
            null -> {
                // No state available (e.g., spotifyViewModel is null)
                showLoadingDialog = false
            }
        }
    }

    // Toggleable settings rendered on screen
    val darkTheme = settingsRepository.darkTheme.collectAsState(true)
    val themeColor = settingsRepository.themeColor.collectAsState(0)
    val autoSignIn = settingsRepository.autoSignIn.collectAsState(false)
    val skipOnRate = settingsRepository.skipOnRate.collectAsState(false)
    val queueSkip = settingsRepository.queueSkip.collectAsState(false)
    val libraryImageUri = settingsRepository.libraryImageUri.collectAsState(true)

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            MyButton(
                onClick = { onExportClick() },
                text = "Export Database"
            )
            MyButton(
                onClick = { onImportClick() },
                text = "Import Database"
            )
        }

        // Create Playlist Button
        if (spotifyViewModel != null && playlistFilterService != null) {
            MyButton(
                onClick = { showPlaylistDialog = true },
                text = "Create New Playlist",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        MySwitch(
            leftText = "Light Theme",
            rightText = "Dark Theme",
            checked = darkTheme.value,
            onCheckedChange = { newState ->
                scope.launch {
                    settingsRepository.setDarkTheme(newState)
                }
            },
            modifier = Modifier.padding(start = 16.dp)
        )

        ThemeSelector(
            currentTheme = themeColor.value,
            onThemeSelected = { newTheme ->
                scope.launch {
                    settingsRepository.setThemeColor(newTheme)
                }
            },
            modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 12.dp)
        )

        BinarySetting(
            displayText = "Keep me signed in (to Spotify)",
            state = autoSignIn.value,
            toggleState = { newState ->
                scope.launch {
                    settingsRepository.setAutoSignIn(newState)
                }
            }
        )
        BinarySetting(
            displayText = "Automatically skip to next on song rate",
            state = skipOnRate.value,
            toggleState = { newState ->
                scope.launch {
                    settingsRepository.setSkipOnRate(newState)
                }
            }
        )
        BinarySetting(
            displayText = "Set play button to queue + skip",
            state = queueSkip.value,
            toggleState = { newState ->
                scope.launch {
                    settingsRepository.setQueueSkip(newState)
                }
            }
        )
        BinarySetting(
            displayText = "Show song images in Library",
            state = libraryImageUri.value,
            toggleState = { newState ->
                scope.launch {
                    settingsRepository.setLibraryImageUri(newState)
                }
            }
        )
    }

    // Playlist Creation Dialog
    if (showPlaylistDialog && spotifyViewModel != null && playlistFilterService != null) {
        CreatePlaylistDialog(
            onConfirm = { config ->
                spotifyViewModel.onEvent(SpotifyEvent.CreatePlaylist(config))
                showPlaylistDialog = false
            },
            onDismiss = {
                showPlaylistDialog = false
            },
            onPreviewCount = { config ->
                playlistFilterService.getFilteredSongCount(config)
            }
        )
    }

    // Loading Dialog
    if (showLoadingDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Creating Playlist") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Text("Please wait...")
                }
            },
            confirmButton = { }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Success!") },
            text = { Text("Playlist '$successPlaylistName' created successfully!") },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// Previews
@PreviewSuite
@Composable
fun SettingsScreenPreviews() {
    MyPreview {
        SettingsScreen()
    }
}
