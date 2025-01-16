package com.example.ratify.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ratify.spotify.SpotifyViewModel

@Composable
fun MainScreen(
    spotifyViewModel: SpotifyViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    val navController = rememberNavController()
    val startDestination = MusicTarget
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Controls pages that show up on bottom nav bar (portrait) and nav drawer (landscape)
    val items = listOf(MusicTarget, LibraryTarget, SettingsTarget)

    if (isLandscape) {
        // Use Navigation Drawer in landscape mode
        Scaffold() { innerPadding ->
            NavigationDrawer(
                navController,
                startDestination,
                items
            ) {
                NavigationHost(
                    navController,
                    startDestination,
                    modifier = Modifier.padding(innerPadding),
                    spotifyViewModel,
                    onExportClick = onExportClick,
                    onImportClick = onImportClick
                )
            }
        }
    } else {
        // Use Bottom Navigation Bar in portrait mode
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController,
                    startDestination,
                    items
                )
            }
        ) { innerPadding ->
            NavigationHost(
                navController,
                startDestination,
                modifier = Modifier.padding(innerPadding),
                spotifyViewModel,
                onExportClick = onExportClick,
                onImportClick = onImportClick
            )
        }
    }
}