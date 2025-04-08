package com.example.ratify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.ratify.spotify.ISpotifyViewModel

@Composable
fun MainScreen(
    spotifyViewModel: ISpotifyViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    val navController = rememberNavController()

    // Controls pages that show up on bottom nav bar (portrait) and nav drawer (landscape)
    val targets = listOf(
        MusicNavigationTarget,
        LibraryNavigationTarget,
        FavoritesNavigationTarget,
        SettingsNavigationTarget)
    val startTarget = MusicNavigationTarget

    NavigationRenderer(
        navController = navController,
        spotifyViewModel = spotifyViewModel,
        navigationTargets = targets,
        onExportClick = onExportClick,
        onImportClick = onImportClick,
        startNavigationTarget = startTarget
    )
}