package com.example.ratify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ratify.domain.CountViewModelProvider
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.ui.screens.CountScreen
import com.example.ratify.ui.screens.HomeScreen
import com.example.ratify.ui.screens.ProfileScreen
import com.example.ratify.ui.screens.SettingsScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    spotifyViewModel: SpotifyViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<MusicTarget> { HomeScreen(spotifyViewModel = spotifyViewModel, onExportClick = onExportClick, onImportClick = onImportClick) }
        composable<LibraryTarget> { ProfileScreen(spotifyViewModel = spotifyViewModel) }
        composable<SettingsTarget> { SettingsScreen() }
    }
}