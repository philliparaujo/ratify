package com.example.ratify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.spotify.SpotifyAuthHelper
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotify.SpotifyViewModelFactory
import com.example.ratify.spotifydatabase.DatabaseIOHelper
import com.example.ratify.ui.navigation.MainScreen
import com.example.ratify.ui.theme.RatifyTheme

class MainActivity : ComponentActivity() {
    private val spotifyViewModel: SpotifyViewModel by viewModels {
        SpotifyViewModelFactory(
            application,
            SongDatabaseProvider.getDatabase(this).dao
        )
    }
    private lateinit var spotifyAuthHelper: SpotifyAuthHelper
    private lateinit var databaseIOHelper: DatabaseIOHelper

    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database IO helper
        databaseIOHelper = DatabaseIOHelper(
            this,
            onExportComplete = { success ->
                spotifyViewModel.showSnackbar(if (success) "Database exported successfully!" else "Failed to export database")
            },
            onImportComplete = { success ->
                spotifyViewModel.showSnackbar(if (success) "Database imported successfully!" else "Failed to import database")
            }
        )

        // On successful DatabaseIO import, the app restarts to display the up-to-date database
        // on the Library page. But the Snackbar we wish to show does not persist on app restarts.
        // This allows us to save the Snackbar and display it on app restart.
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val snackbarMessage = sharedPrefs.getString("pendingSnackbar", null)
        if (snackbarMessage != null) {
            spotifyViewModel.showSnackbar(snackbarMessage)
            sharedPrefs.edit().remove("pendingSnackbar").apply()
        }

        // Initialize auth helper, launch authentication if not already connected
        if (spotifyViewModel.spotifyConnectionState.value != true) {
            spotifyAuthHelper = SpotifyAuthHelper(this, spotifyViewModel)
            spotifyViewModel.authRequest.observe(this) { request ->
                spotifyAuthHelper.launchAuth(request)
            }
        }

        setContent {
            RatifyTheme {
               MainScreen(
                   spotifyViewModel = spotifyViewModel,
                   onExportClick = { databaseIOHelper.exportDatabase() },
                   onImportClick = { databaseIOHelper.importDatabase() }
               )
            }
        }

        // Set status bar content (top/bottom) to white to match dark app background
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    }

    override fun onResume() {
        super.onResume()

        // Sync playback position by asking Spotify, prevents de-sync when app in background
        spotifyViewModel.syncPlaybackPositionNow()
    }
}