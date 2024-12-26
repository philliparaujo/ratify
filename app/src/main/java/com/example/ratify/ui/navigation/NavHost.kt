package com.example.ratify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ratify.ui.CountViewModelProvider
import com.example.ratify.ui.screens.CountScreen
import com.example.ratify.ui.screens.HomeScreen
import com.example.ratify.ui.screens.ProfileScreen
import com.example.ratify.ui.screens.SettingsScreen

@Composable
fun NavigationHost(navController: NavHostController, startDestination: Screen, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val countViewModel = remember { CountViewModelProvider.provideCountViewModel(context) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
        composable(Screen.CountManager.route) { CountScreen(viewModel = countViewModel) }
    }
}