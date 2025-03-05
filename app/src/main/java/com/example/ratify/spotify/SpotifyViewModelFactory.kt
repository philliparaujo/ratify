package com.example.ratify.spotify

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ratify.settings.SettingsManager
import com.example.ratify.spotifydatabase.SongDao

class SpotifyViewModelFactory(
    private val application: Application,
    private val dao: SongDao,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpotifyViewModel::class.java)) {
            return SpotifyViewModel(application, dao, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}