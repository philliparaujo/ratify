package com.example.ratify.spotify

import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.SearchType
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SortType
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.Track

sealed interface SpotifyEvent {
    // Authentication and connection
    data object GenerateAuthorizationRequest: SpotifyEvent
    data object ConnectSpotify: SpotifyEvent
    data object DisconnectSpotify: SpotifyEvent

    // Player
    data class PlayPlaylist(val playlistUri: String): SpotifyEvent
    data class PlaySong(val songUri: String): SpotifyEvent
    data object Pause: SpotifyEvent
    data object Resume: SpotifyEvent
    data object SkipNext: SpotifyEvent
    data object SkipPrevious: SpotifyEvent
//    data class QueueTrack(val trackUri: String): SpotifyEvent

    // State updates
    data class UpdateSearchType(val searchType: SearchType): SpotifyEvent
    data class UpdateSortType(val sortType: SortType): SpotifyEvent
    data class UpdateCurrentRating(val rating: Rating?): SpotifyEvent
    data class UpdateShowSongDialog(val showSongDialog: Song?): SpotifyEvent
    data class UpdateVisualizerShowing(val visualizerShowing: Boolean): SpotifyEvent

    // Database updates
    data class UpsertSong(val track: Track, val rating: Rating?, val lastRatedTs: Long?, val lastPlayedTs: Long?, val timesPlayed: Int): SpotifyEvent
    data class DeleteSong(val song: Song): SpotifyEvent
    data class DeleteSongsWithNullRating(val exceptName: String, val exceptArtists: List<Artist>): SpotifyEvent
    data class UpdateLastPlayedTs(val name: String, val artists: List<Artist>, val timesPlayed: Int, val lastPlayedTs: Long?): SpotifyEvent
    data class UpdateRating(val name: String, val artists: List<Artist>, val rating: Rating?, val lastRatedTs: Long?): SpotifyEvent
}