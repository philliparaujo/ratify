package com.example.ratify

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.spotify.SpotifyAuthHelper
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotify.SpotifyViewModelFactory
import com.example.ratify.ui.navigation.MainScreen

class MainActivity : ComponentActivity() {
    private val spotifyViewModel: SpotifyViewModel by viewModels {
        SpotifyViewModelFactory(
            application,
            SongDatabaseProvider.getDatabase(this).dao
        )
    }
    private lateinit var spotifyAuthHelper: SpotifyAuthHelper

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            val success = SongDatabaseProvider.exportDatabase(this, uri, contentResolver)
            showToast(if (success) "Database exported successfully!" else "Failed to export database")
        }
    }
    private val importLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val success = SongDatabaseProvider.importDatabase(this, uri)
            showToast(if (success) "Database imported successfully!" else "Failed to import database")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

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
               spotifyViewModel = spotifyViewModel,
               onExportClick = { exportLauncher.launch("songs.db") },
               onImportClick = { importLauncher.launch(arrayOf("*/*"))}
           )
        }
    }
}