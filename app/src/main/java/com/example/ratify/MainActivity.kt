package com.example.ratify

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.services.ServiceApp
import com.example.ratify.core.model.PrimaryColor
import com.example.ratify.settings.SettingsManager
import com.example.ratify.spotify.SpotifyAuthHelper
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotify.SpotifyViewModelFactory
import com.example.ratify.database.DatabaseIOHelper
import com.example.ratify.ui.navigation.MainScreen
import com.example.ratify.ui.theme.RatifyTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val settingsManager by lazy { SettingsManager(this) }
    private val spotifyViewModel: SpotifyViewModel by viewModels {
        SpotifyViewModelFactory(
            application,
            SongDatabaseProvider.getDatabase(this).dao,
            settingsManager
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

        (application as ServiceApp).spotifyViewModel = spotifyViewModel

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

        // The quick media controls needs post notification permissions, so on app launch we need
        // to request permissions for it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
                ),
                0
            )
        }

        setContent {
            val darkTheme by settingsManager.darkTheme.collectAsState(true)
            val themeColor by settingsManager.themeColor.collectAsState(PrimaryColor.DEFAULT.ordinal)

            RatifyTheme(
                darkTheme = darkTheme,
                themeColor = themeColor
            ) {
               MainScreen(
                   spotifyViewModel = spotifyViewModel,
                   onExportClick = { databaseIOHelper.exportDatabase() },
                   onImportClick = { databaseIOHelper.importDatabase() }
               )
            }

            // Dynamically update status bar appearance based on theme
            LaunchedEffect(darkTheme) {
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Sync playback position by asking Spotify, prevents de-sync when app in background
        spotifyViewModel.syncPlaybackPositionNow()
    }

    override fun onStart() {
        super.onStart()

        // If the app launch has autoSignIn set to true, automatically connect to Spotify
        val connected = spotifyViewModel.spotifyConnectionState.value == true
        if (!connected) {
            lifecycleScope.launch {
                // .first() prevents auto sign-in on setting toggle
                val autoSignIn = settingsManager.autoSignIn.first()
                if (autoSignIn) {
                    spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
                }
            }
        }
    }
}