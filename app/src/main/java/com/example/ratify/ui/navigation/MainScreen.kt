package com.example.ratify.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ratify.spotify.SpotifyViewModel

@Composable
fun MainScreen(
    spotifyViewModel: SpotifyViewModel,
    onExportClick: (Uri) -> Unit,
    onImportClick: (Uri) -> Unit
) {
    val navController = rememberNavController()
    val startDestination = HomeTarget

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController,
                startDestination
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