package com.example.ratify.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.ratify.R

class RatingService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val currentRating = intent?.getStringExtra(CURRENT_RATING_EXTRA) ?: nullTextViewValue

        when (intent?.action) {
            Actions.START.toString() -> start(currentRating)
            Actions.STOP.toString() -> stopSelf()
            Actions.UPDATE.toString() -> update(currentRating)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        fun createCustomRemoteView(context: Context, currentRating: String): RemoteViews {
            val packageName = context.packageName
            val remoteViews = RemoteViews(packageName, R.layout.custom_notification)

            val sharedPrefs = context.getSharedPreferences(BUTTON_SHARED_PREFS, Context.MODE_PRIVATE)

            // Set up textView with proper value
            remoteViews.setTextViewText(textViewId, currentRating)

            // Reset all button preferences to default values
            val editor = sharedPrefs.edit()
            buttonIds.zip(defaultButtonValues).forEach { (id, defaultValue) ->
                editor.putInt(id.toString(), defaultValue)
            }

            // If current rating matches default button value, flip that button's preference
            val ratingInt = currentRating.toIntOrNull()
            if (ratingInt != null) {
                val matchingIndex = defaultButtonValues.indexOf(ratingInt)
                if (matchingIndex != -1) {
                    val id = buttonIds[matchingIndex]
                    val flippedValue = flipButtonValue(ratingInt)
                    editor.putInt(id.toString(), flippedValue)
                }
            }
            editor.apply()

            // Update button UI + intents
            buttonIds.forEach { id ->
                val buttonValue = sharedPrefs.getInt(id.toString(), -1)
                remoteViews.setTextViewText(id, buttonValue.toString())

                val intent = Intent(context, RatingBroadcastReceiver::class.java).apply {
                    putExtra(BUTTON_ID_EXTRA, id)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, id, intent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    else PendingIntent.FLAG_UPDATE_CURRENT
                )
                remoteViews.setOnClickPendingIntent(id, pendingIntent)
            }

            return remoteViews
        }
    }

    private fun start(currentRating: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContent(createCustomRemoteView(this, currentRating))
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun update(currentRating: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContent(createCustomRemoteView(this, currentRating))
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    enum class Actions {
        START,
        STOP,
        UPDATE,
    }
}