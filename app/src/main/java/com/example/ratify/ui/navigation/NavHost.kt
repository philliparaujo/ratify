package com.example.ratify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.ui.screens.MusicScreen
import com.example.ratify.ui.screens.LibraryScreen
import com.example.ratify.ui.screens.SettingsScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    startNavigationTarget: NavigationTarget,
    modifier: Modifier = Modifier,
    spotifyViewModel: SpotifyViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startNavigationTarget,
        modifier = modifier
    ) {
        composable<MusicNavigationTarget> { MusicScreen(spotifyViewModel = spotifyViewModel) }
        composable<LibraryNavigationTarget> { LibraryScreen(spotifyViewModel = spotifyViewModel, navController = navController) }
        composable<SettingsNavigationTarget> { SettingsScreen(onExportClick = onExportClick, onImportClick = onImportClick) }
    }
}