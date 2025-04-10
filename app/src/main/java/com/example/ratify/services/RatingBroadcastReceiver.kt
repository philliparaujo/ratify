package com.example.ratify.services

import com.example.ratify.repository.SongRepository
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ratify.core.model.Rating
import com.example.ratify.database.Converters
import com.example.ratify.repository.StateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class RatingBroadcastReceiver : BroadcastReceiver() {
    // Triggers on button click within notification
    override fun onReceive(context: Context, intent: Intent?) {
        // Fetch buttonId and buttonValue (current rating)
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
        val koin = GlobalContext.get()
        val songRepository = koin.get<SongRepository>()
        val stateRepository = koin.get<StateRepository>()

        stateRepository.updateCurrentRating(Rating.from(buttonValue))

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = context.getSharedPreferences(PLAYER_STATE_SHARED_PREFS, Context.MODE_PRIVATE)
            val name = prefs.getString(TRACK_NAME_SHARED_PREFS, null)
            val artists = prefs.getString(TRACK_ARTISTS_SHARED_PREFS, null)

            val converters = Converters()

            if (name != null && artists != null) {
                songRepository.UpdateRating(
                    name = name,
                    artists = converters.toArtistList(artists)!!,
                    rating = Rating.from(buttonValue),
                    lastRatedTs = System.currentTimeMillis()
                )
            } else {
                Log.e("RatingBroadcastReceiver", "PlayerState or Track is null. Cannot update rating.")
            }
        }


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


