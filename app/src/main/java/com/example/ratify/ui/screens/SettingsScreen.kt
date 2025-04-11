package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.mocks.LANDSCAPE_DEVICE
import com.example.ratify.mocks.MyPreview
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.ui.components.BinarySetting
import com.example.ratify.ui.components.MyButton
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.components.ThemeSelector
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onExportClick: () -> Unit = {},
    onImportClick: () -> Unit = {}
) {
    val settingsRepository: SettingsRepository = LocalSettingsRepository.current

    val scope = rememberCoroutineScope()

    val autoSignIn = settingsRepository.autoSignIn.collectAsState(false)
    val skipOnRate = settingsRepository.skipOnRate.collectAsState(false)
    val queueSkip = settingsRepository.queueSkip.collectAsState(false)
    val libraryImageUri = settingsRepository.libraryImageUri.collectAsState(true)
    val darkTheme = settingsRepository.darkTheme.collectAsState(true)
    val themeColor = settingsRepository.themeColor.collectAsState(0)

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
    ) {
        Column {
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
    }
}

@Preview(name = "Dark Settings Screen")
@Composable
fun DarkSettingsScreenPreview() {
    MyPreview(darkTheme = true) {
        SettingsScreen()
    }
}

@Preview(name = "Light Settings Screen")
@Composable
fun LightSettingsScreenPreview() {
    MyPreview(darkTheme = false) {
        SettingsScreen()
    }
}

@Preview(
    name = "Dark Landscape Settings Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun DarkLandscapeSettingsScreenPreview() {
    MyPreview(darkTheme = true) {
        SettingsScreen()
    }
}

@Preview(
    name = "Light Landscape Settings Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun LightLandscapeSettingsScreenPreview() {
    MyPreview(darkTheme = false) {
        SettingsScreen()
    }
}

