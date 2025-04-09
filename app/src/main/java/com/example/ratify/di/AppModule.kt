package com.example.ratify.di

import SongRepository
import androidx.room.Room
import com.example.ratify.database.SongDao
import com.example.ratify.database.SongDatabase
import com.example.ratify.settings.ISettingsManager
import com.example.ratify.settings.SettingsManager
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotify.StateRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ISettingsManager> { SettingsManager(androidApplication()) }

    single<SongDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            SongDatabase::class.java,
            "songs.db"
        ).build()
    }

    single<SongDao> { get<SongDatabase>().dao }

    single<SongRepository> {
        SongRepository(get())
    }

    single<StateRepository> {
        StateRepository(get())
    }

    viewModel {
        SpotifyViewModel(
            application = androidApplication(),
            songRepository = get(),
            stateRepository = get(),
            settingsManager = get()
        )
    }
}