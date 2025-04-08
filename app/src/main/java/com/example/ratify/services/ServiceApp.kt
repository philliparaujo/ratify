package com.example.ratify.services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.di.appModule
import com.example.ratify.settings.SettingsManager
import com.example.ratify.spotify.SpotifyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ServiceApp: Application() {
//    lateinit var settingsManager: SettingsManager
//        private set
//    lateinit var spotifyViewModel: SpotifyViewModel
//
//    val database by lazy { SongDatabaseProvider.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
//        settingsManager = SettingsManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        startKoin {
            androidLogger()
            androidContext(this@ServiceApp)
            modules(appModule)
        }
    }
}