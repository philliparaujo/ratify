package com.example.ratify.ui.screens

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.services.MyService
import com.example.ratify.settings.PrimaryColor
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.SongState
import com.example.ratify.ui.components.BinarySetting
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.components.ThemeSelector
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

    val autoSignIn = settings?.autoSignIn?.collectAsState(false)
    val skipOnRate = settings?.skipOnRate?.collectAsState(false)
    val queueSkip = settings?.queueSkip?.collectAsState(false)
    val libraryImageUri = settings?.libraryImageUri?.collectAsState(true)
    val darkTheme = settings?.darkTheme?.collectAsState(true)
    val themeColor = settings?.themeColor?.collectAsState(0)

    val contextWrapper = ContextWrapper(LocalContext.current)

    val songState = spotifyViewModel?.state?.collectAsState(initial = SongState())

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
    ) {
        Column {
            Button(onClick = {
                Intent(contextWrapper, MyService::class.java).also {
                    it.action = MyService.Actions.START.toString()
                    Log.d("SettingsScreen", songState?.value?.currentRating?.value.toString())
                    it.putExtra("current_rating", songState?.value?.currentRating?.value.toString() ?: "-")
                    contextWrapper.startService(it)
                }
            }) {
                Text("Start service")
            }
            Button(onClick = {
                Intent(contextWrapper, MyService::class.java).also {
                    it.action = MyService.Actions.UPDATE.toString()
                    Log.d("SettingsScreen", songState?.value?.currentRating?.value.toString())
                    it.putExtra("current_rating", songState?.value?.currentRating?.value.toString() ?: "-")
                    contextWrapper.startService(it)
                }
            }) {
                Text("Update service")
            }
            Button(onClick = {
                Intent(contextWrapper, MyService::class.java).also {
                    it.action = MyService.Actions.STOP.toString()
                    contextWrapper.startService(it)
                }
            }) {
                Text("Stop service")
            }
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    "Light Theme",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = darkTheme?.value ?: true,
                    onCheckedChange = { newState ->
                        scope.launch {
                            settings?.setDarkTheme(newState)
                        }
                    },
                )
                Text(
                    "Dark Theme",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            ThemeSelector(
                currentTheme = themeColor?.value ?: PrimaryColor.DEFAULT.ordinal,
                onThemeSelected = { newTheme ->
                    scope.launch {
                        settings?.setThemeColor(newTheme)
                    }
                },
                modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 12.dp)
            )

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
            BinarySetting(
                displayText = "Show song images in Library",
                state = libraryImageUri?.value ?: true,
                toggleState = { newState ->
                    scope.launch {
                        settings?.setLibraryImageUri(newState)
                    }
                }
            )
        }
    }
}

@Preview(name = "Dark Settings Screen")
@Composable
fun DarkSettingsScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        SettingsScreen(
            spotifyViewModel = null,
            onExportClick = { },
            onImportClick = { }
        )
    }
}

@Preview(name = "Light Settings Screen")
@Composable
fun LightSettingsScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        SettingsScreen(
            spotifyViewModel = null,
            onExportClick = { },
            onImportClick = { }
        )
    }
}

@Preview(
    name = "Dark Landscape Settings Screen",
    device = landscapeDevice
)
@Composable
fun DarkLandscapeSettingsScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        SettingsScreen(
            spotifyViewModel = null,
            onExportClick = { },
            onImportClick = { }
        )
    }
}

@Preview(
    name = "Light Landscape Settings Screen",
    device = landscapeDevice
)
@Composable
fun LightLandscapeSettingsScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        SettingsScreen(
            spotifyViewModel = null,
            onExportClick = { },
            onImportClick = { }
        )
    }
}

