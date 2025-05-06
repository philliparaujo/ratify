package com.example.ratify

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.ratify.core.model.PrimaryColor
import com.example.ratify.database.DatabaseIOHelper
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.spotify.SpotifyAuthHelper
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.ui.navigation.MainScreen
import com.example.ratify.ui.theme.RatifyTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val spotifyViewModel: SpotifyViewModel by viewModel()
    private val songRepository: SongRepository by inject()
    private val stateRepository: StateRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private lateinit var spotifyAuthHelper: SpotifyAuthHelper
    private lateinit var databaseIOHelper: DatabaseIOHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // On successful DatabaseIO import, the app restarts to display the up-to-date database
        // on the Library page. But the Snackbar we wish to show does not persist on app restarts.
        // This allows us to save the Snackbar and display it on app restart.
        val pendingSnackbar = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("pendingSnackbar", null)

        // Initialize database IO helper
        databaseIOHelper = DatabaseIOHelper(
            this,
            onExportComplete = { success ->
                stateRepository.showSnackbar(if (success) "Database exported successfully!" else "Failed to export database")
            },
            onImportComplete = { success ->
                stateRepository.showSnackbar(if (success) "Database imported successfully!" else "Failed to import database")
            }
        )

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
            CompositionLocalProvider(
                LocalSpotifyViewModel provides spotifyViewModel,
                LocalSongRepository provides songRepository,
                LocalStateRepository provides stateRepository,
                LocalSettingsRepository provides settingsRepository
            ) {
                val darkTheme by settingsRepository.darkTheme.collectAsState(true)
                val themeColor by settingsRepository.themeColor.collectAsState(PrimaryColor.DEFAULT.ordinal)

                // Display the Snackbar we previously fetched
                LaunchedEffect(pendingSnackbar) {
                    if (pendingSnackbar != null) {
                        stateRepository.showSnackbar(pendingSnackbar)

                        // Remove the string so that on future launch, Snackbar not displayed
                        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit().remove("pendingSnackbar").apply()
                    }
                }

                RatifyTheme(
                    darkTheme = darkTheme,
                    themeColor = themeColor
                ) {
                    MainScreen(
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
                val autoSignIn = settingsRepository.autoSignIn.first()
                if (autoSignIn) {
                    spotifyViewModel.onEvent(SpotifyEvent.GenerateAuthorizationRequest)
                }
            }
        }
    }

    fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        Runtime.getRuntime().exit(0) // Fully restart app
    }
}