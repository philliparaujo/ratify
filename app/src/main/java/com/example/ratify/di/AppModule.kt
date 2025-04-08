package com.example.ratify.di

import androidx.room.Room
import com.example.ratify.database.SongDatabase
import com.example.ratify.settings.ISettingsManager
import com.example.ratify.settings.SettingsManager
import com.example.ratify.spotify.SpotifyViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ISettingsManager> { SettingsManager(androidApplication()) }

    single {
        Room.databaseBuilder(
            androidApplication(),
            SongDatabase::class.java,
            "songs.db"
        ).build()
    }

    single { get<SongDatabase>().dao }

    viewModel {
        SpotifyViewModel(
            application = androidApplication(),
            dao = get(),
            settingsManager = get()
        )
    }
}