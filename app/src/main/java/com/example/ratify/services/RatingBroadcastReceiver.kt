package com.example.ratify.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ratify.R
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotifydatabase.Rating

class RatingBroadcastReceiver : BroadcastReceiver() {
    // Triggers on button click within notification
    override fun onReceive(context: Context, intent: Intent?) {
        // Fetch buttonId and buttonValue
        val buttonId = intent?.getIntExtra(BUTTON_ID_EXTRA, -1) ?: -1
        if (buttonId == -1) {
            Log.d("MyBroadcastReceiver", "button id received as -1")
            return
        }

        val sharedPrefs = context.getSharedPreferences(BUTTON_SHARED_PREFS, Context.MODE_PRIVATE)
        val buttonValue = sharedPrefs.getInt(buttonId.toString(), -1)
        if (buttonValue == -1) {
            Log.d("MyBroadcastReceiver", "button value received as -1")
            return
        }

        // Update UI and database ratings
        val spotifyViewModel = (context.applicationContext as ServiceApp).spotifyViewModel
        val playerState = spotifyViewModel.playerState.value
        spotifyViewModel.onEvent(SpotifyEvent.UpdateCurrentRating(Rating.from(buttonValue)))
        spotifyViewModel.onEvent(SpotifyEvent.UpdateRating(
            name = playerState!!.track.name,
            artists = playerState.track.artists,
            rating = Rating.from(buttonValue),
            lastRatedTs = System.currentTimeMillis()
        ))

        // Calculate new button value, update shared preferences of button and text view
        val newValue = flipButtonValue(buttonValue)
        Log.d("MyBroadcastReceiver", "Button ID: $buttonId, Toggled Value: $buttonValue -> $newValue")
        sharedPrefs.edit().putString(textViewId.toString(), buttonValue.toString()).apply()
        sharedPrefs.edit().putInt(buttonId.toString(), newValue).apply()

        // Create and send an updated notification
        val remoteViews = RatingService.createCustomRemoteView(context, buttonValue.toString())
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingNotification = setupNotification(
            context = context,
            remoteViews = remoteViews,
            onlyAlertOnce = true
        ).build()

        notificationManager.notify(NOTIFICATION_ID, existingNotification)
    }
}


