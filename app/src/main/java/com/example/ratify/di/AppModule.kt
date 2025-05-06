package com.example.ratify.di

import com.example.ratify.database.SongDao
import com.example.ratify.database.SongDatabase
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.spotify.SpotifyViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<SongDatabase> {
        SongDatabaseProvider.getDatabase(androidApplication())
    }

    single<SongDao> {
        get<SongDatabase>().dao
    }

    single<SongRepository> {
        SongRepository(get<SongDao>())
    }

    single<StateRepository> {
        StateRepository(get<SongRepository>())
    }

    single<SettingsRepository> {
        SettingsRepository(androidApplication())
    }

    viewModel {
        SpotifyViewModel(
            application = androidApplication(),
            songRepository = get(),
            stateRepository = get()
        )
    }
}