package com.example.ratify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.spotify.SpotifyAuthHelper
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotify.SpotifyViewModelFactory
import com.example.ratify.ui.navigation.MainScreen

class MainActivity : ComponentActivity() {
//    private val spotifyViewModel : SpotifyViewModel by viewModels()
    private val spotifyViewModel: SpotifyViewModel by viewModels {
        SpotifyViewModelFactory(
            application,
            SongDatabaseProvider.getDatabase(this).dao
        )
}
    private lateinit var spotifyAuthHelper: SpotifyAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize auth helper, launch authentication if not already connected
        if (spotifyViewModel.spotifyConnectionState.value != true) {
            spotifyAuthHelper = SpotifyAuthHelper(this, spotifyViewModel)
            spotifyViewModel.authRequest.observe(this) { request ->
                spotifyAuthHelper.launchAuth(request)
            }
        }

        setContent {
           MainScreen(
               spotifyViewModel = spotifyViewModel
           )
        }
    }
}