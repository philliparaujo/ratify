package com.example.ratify.di

import com.example.ratify.database.SongDao
import com.example.ratify.database.SongDatabase
import com.example.ratify.database.SongDatabaseProvider
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.services.PlaylistFilterService
import com.example.ratify.spotify.SpotifyViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Defines where all component injections occur, providing global access to these singletons
// Dependency injection prevents passing in parameters like SpotifyViewModel into every file
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

    single<PlaylistFilterService> {
        PlaylistFilterService(get<SongRepository>())
    }

    viewModel {
        SpotifyViewModel(
            application = androidApplication(),
            songRepository = get(),
            stateRepository = get(),
            playlistFilterService = get()
        )
    }
}