package com.example.ratify.spotify

sealed interface SpotifyEvent {
    // Authentication and connection
    data object GenerateAuthorizationRequest: SpotifyEvent
    data object ConnectAppRemote: SpotifyEvent
    data object DisconnectAppRemote: SpotifyEvent

    // Player
    data class PlayPlaylist(val playlistUri: String): SpotifyEvent
    data class PlaySong(val songUri: String, val songName: String): SpotifyEvent
    data object Pause: SpotifyEvent
    data object Resume: SpotifyEvent
    data object SkipNext: SpotifyEvent
    data object SkipPrevious: SpotifyEvent
    data class SeekTo(val positionMs: Long): SpotifyEvent
    data class QueueTrack(val trackUri: String, val trackName: String): SpotifyEvent

    data object PlayerEventWhenNotConnected: SpotifyEvent
}