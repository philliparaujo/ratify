package com.example.ratify.spotify

sealed interface SpotifyEvent {
    // Authentication and connection
    data object GenerateAuthorizationRequest: SpotifyEvent
    data object ConnectSpotify: SpotifyEvent
    data object DisconnectSpotify: SpotifyEvent

    // User information
    data object GetUserCapabilities: SpotifyEvent

    // Player
    data class PlayPlaylist(val playlistUri: String): SpotifyEvent
    data object Pause: SpotifyEvent
    data object Resume: SpotifyEvent
    data object SkipNext: SpotifyEvent
    data object SkipPrevious: SpotifyEvent
//    data class QueueTrack(val trackUri: String): SpotifyEvent
}