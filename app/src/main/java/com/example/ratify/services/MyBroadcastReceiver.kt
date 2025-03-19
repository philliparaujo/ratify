package com.example.ratify.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ratify.R

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Fetch buttonId and buttonValue
        val buttonId = intent?.getIntExtra("button_id", -1) ?: -1
        if (buttonId == -1) {
            Log.d("MyBroadcastReceiver", "button id received as -1")
            return
        }

        val sharedPrefs = context.getSharedPreferences("button_prefs", Context.MODE_PRIVATE)
        val buttonValue = sharedPrefs.getInt(buttonId.toString(), -1)
        if (buttonValue == -1) {
            Log.d("MyBroadcastReceiver", "button value received as -1")
            return
        }

        // Calculate new button value and save it to shared preferences
        sharedPrefs.edit().putString(textViewId.toString(), buttonValue.toString()).apply()

        val newValue = if (buttonValue % 2 == 0) buttonValue - 1 else buttonValue + 1
        Log.d("MyBroadcastReceiver", "Button ID: $buttonId, Toggled Value: $buttonValue -> $newValue")
        sharedPrefs.edit().putInt(buttonId.toString(), newValue).apply()

        // Create and send an updated notification
        val remoteViews = MyService.createCustomRemoteView(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(remoteViews)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, existingNotification)
        Log.d("MyBroadcastReceiver", "sent new notification")
    }
}


