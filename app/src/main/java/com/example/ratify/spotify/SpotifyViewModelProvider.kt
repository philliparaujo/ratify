package com.example.ratify.spotify

import android.app.Application

object SpotifyViewModelProvider {
    fun provideCountViewModel(application: Application): SpotifyViewModel {
        return SpotifyViewModel(application)
    }
}