package com.example.ratify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.services.PlaylistFilterService
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.ui.screens.FavoritesScreen
import com.example.ratify.ui.screens.LibraryScreen
import com.example.ratify.ui.screens.MusicScreen
import com.example.ratify.ui.screens.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun NavigationHost(
    navController: NavHostController,
    startNavigationTarget: NavigationTarget,
    modifier: Modifier = Modifier,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startNavigationTarget,
        modifier = modifier
    ) {
        composable<MusicNavigationTarget> { MusicScreen() }
        composable<LibraryNavigationTarget> { LibraryScreen(navController = navController) }
        composable<FavoritesNavigationTarget> { FavoritesScreen() }
        composable<SettingsNavigationTarget> {
            val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current
            val playlistFilterService: PlaylistFilterService = koinInject()
            SettingsScreen(
                onExportClick = onExportClick,
                onImportClick = onImportClick,
                spotifyViewModel = spotifyViewModel,
                playlistFilterService = playlistFilterService
            )
        }
    }
}