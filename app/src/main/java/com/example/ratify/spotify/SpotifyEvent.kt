package com.example.ratify.spotify

import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SortType

sealed interface SpotifyEvent {
    // Authentication and connection
    data object GenerateAuthorizationRequest: SpotifyEvent
    data object ConnectSpotify: SpotifyEvent
    data object DisconnectSpotify: SpotifyEvent

    // Player
    data class PlayPlaylist(val playlistUri: String): SpotifyEvent
    data object Pause: SpotifyEvent
    data object Resume: SpotifyEvent
    data object SkipNext: SpotifyEvent
    data object SkipPrevious: SpotifyEvent
//    data class QueueTrack(val trackUri: String): SpotifyEvent

    // Database updates
    data class UpsertSong(val song: Song): SpotifyEvent
    data class DeleteSong(val song: Song): SpotifyEvent
    data class UpdateLastPlayedTs(val uri: String, val lastPlayedTs: Long?): SpotifyEvent
    data class UpdateRating(val uri: String, val rating: Rating?, val lastRatedTs: Long?): SpotifyEvent
    data class SortSongs(val sortType: SortType): SpotifyEvent
    data class UpdateCurrentRating(val rating: Rating?): SpotifyEvent
}