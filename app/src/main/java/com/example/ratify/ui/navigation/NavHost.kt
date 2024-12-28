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
    spotifyViewModel: SpotifyViewModel
) {
    val context = LocalContext.current
    val countViewModel = remember { CountViewModelProvider.provideCountViewModel(context) }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<HomeTarget> { HomeScreen(spotifyViewModel = spotifyViewModel) }
        composable<ProfileTarget> { ProfileScreen() }
        composable<SettingsTarget> { SettingsScreen() }
        composable<CountManagerTarget> { CountScreen(viewModel = countViewModel) }
    }
}