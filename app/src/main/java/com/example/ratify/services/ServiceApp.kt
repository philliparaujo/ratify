package com.example.ratify.services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.ratify.R

const val CHANNEL_ID = "running_channel"
const val NOTIFICATION_ID = 1
val buttonIds = listOf(R.id.button2, R.id.button4, R.id.button6, R.id.button8, R.id.button10)
val textViewId = R.id.currentRating
val defaultButtonValues = listOf(2, 4, 6, 8, 10)

class ServiceApp: Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Name",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}