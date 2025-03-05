package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.ui.components.BinarySetting
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.theme.RatifyTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    spotifyViewModel: SpotifyViewModel?,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val settings = spotifyViewModel?.settings
    val scope = rememberCoroutineScope()

    val autoSignIn = settings?.autoSignIn?.collectAsState(initial = false)
    val skipOnRate = settings?.skipOnRate?.collectAsState(initial = false)
    val queueSkip = settings?.queueSkip?.collectAsState(initial = false)

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            Text(text = "Settings Screen", fontSize = 24.sp, modifier = Modifier.padding(16.dp))

            Row(modifier = Modifier.padding(16.dp)) {
                MyButton(
                    onClick = { onExportClick() },
                    text = "Export Database"
                )
                MyButton(
                    onClick = { onImportClick() },
                    text = "Import Database"
                )
            }

            Column {
                BinarySetting(
                    displayText = "Keep me signed in (to Spotify)",
                    state = autoSignIn?.value ?: false,
                    toggleState = { newState ->
                        scope.launch {
                            settings?.setAutoSignIn(newState)
                        }
                    }
                )
                BinarySetting(
                    displayText = "Automatically skip to next on song rate",
                    state = skipOnRate?.value ?: false,
                    toggleState = { newState ->
                        scope.launch {
                            settings?.setSkipOnRate(newState)
                        }
                    }
                )
                BinarySetting(
                    displayText = "Set play button to queue + skip",
                    state = queueSkip?.value ?: false,
                    toggleState = { newState ->
                        scope.launch {
                            settings?.setQueueSkip(newState)
                        }
                    }
                )
            }
        }
    }
}

@Preview(name = "Settings Screen")
@Composable
fun SettingsScreenPreview() {
    RatifyTheme {
        SettingsScreen(
            spotifyViewModel = null,
            onExportClick = { },
            onImportClick = { }
        )
    }
}

@Preview(
    name = "Landscape Settings Screen",
    device = landscapeDevice
)
@Composable
fun LandscapeSettingsScreenPreview() {
    RatifyTheme {
        SettingsScreen(
            spotifyViewModel = null,
            onExportClick = { },
            onImportClick = { }
        )
    }
}