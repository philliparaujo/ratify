package com.example.ratify.spotify

sealed interface SpotifyEvent {
    data object GenerateAuthorizationRequest: SpotifyEvent
    data object ConnectSpotify: SpotifyEvent
    data class PlayPlaylist(val playlistUri: String): SpotifyEvent
    data object DisconnectSpotify: SpotifyEvent
}