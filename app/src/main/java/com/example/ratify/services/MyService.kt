package com.example.ratify.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.ratify.R

class MyService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        fun createCustomRemoteView(context: Context): RemoteViews {
            val packageName = context.packageName
            val remoteViews = RemoteViews(packageName, R.layout.custom_notification)

            val sharedPrefs = context.getSharedPreferences("button_prefs", Context.MODE_PRIVATE)

            // Set up textView with proper value
            val textViewValue = sharedPrefs.getString(textViewId.toString(), "-")
            remoteViews.setTextViewText(textViewId, textViewValue)

            // Set up buttons with proper values and onClicks
            buttonIds.zip(defaultButtonValues).forEach { (id, defaultValue) ->
                var buttonValue = sharedPrefs.getInt(id.toString(), -1)
                if (buttonValue == -1) {
                    Log.d("MyBroadcastReceiver", "stored button value is -1")
                    sharedPrefs.edit().putInt(id.toString(), defaultValue).apply()
                    buttonValue = defaultValue
                }
                remoteViews.setTextViewText(id, buttonValue.toString())

                val intent = Intent(context, MyBroadcastReceiver::class.java).apply {
                    putExtra("button_id", id)
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

    private fun start() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContent(createCustomRemoteView(this))
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    enum class Actions {
        START,
        STOP
    }
}