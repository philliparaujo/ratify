package com.example.ratify.spotify

import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.database.GroupedSong
import com.example.ratify.core.model.Rating
import com.example.ratify.core.model.SearchType
import com.example.ratify.database.Song
import com.example.ratify.core.model.LibrarySortType
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.Track

sealed interface SpotifyEvent {
    // Authentication and connection
    data object GenerateAuthorizationRequest: SpotifyEvent
    data object ConnectSpotify: SpotifyEvent
    data object DisconnectSpotify: SpotifyEvent

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

    // State updates
    data class UpdateSearchType(val searchType: SearchType): SpotifyEvent
    data class UpdateSearchText(val text: String): SpotifyEvent

    data class UpdateLibrarySortType(val librarySortType: LibrarySortType): SpotifyEvent
    data class UpdateFavoritesSortType(val favoritesSortType: FavoritesSortType): SpotifyEvent
    data class UpdateGroupType(val groupType: GroupType): SpotifyEvent

    data class UpdateLibrarySortAscending(val sortAscending: Boolean): SpotifyEvent
    data class UpdateFavoritesSortAscending(val sortAscending: Boolean): SpotifyEvent

    data class UpdateCurrentRating(val rating: Rating?): SpotifyEvent
    data class UpdateLibraryDialog(val song: Song?): SpotifyEvent
    data class UpdateFavoritesDialog(val groupedSong: GroupedSong?): SpotifyEvent
    data class UpdateVisualizerShowing(val visualizerShowing: Boolean): SpotifyEvent
    data class UpdateMinEntriesThreshold(val newThreshold: Int): SpotifyEvent

    // Database updates
    data class UpsertSong(val track: Track, val rating: Rating?, val lastRatedTs: Long?, val lastPlayedTs: Long?, val timesPlayed: Int): SpotifyEvent
    data class DeleteSong(val song: Song): SpotifyEvent
    data class DeleteSongsWithNullRating(val exceptName: String, val exceptArtists: List<Artist>): SpotifyEvent
    data class UpdateLastPlayedTs(val name: String, val artists: List<Artist>, val timesPlayed: Int, val lastPlayedTs: Long?): SpotifyEvent
    data class UpdateRating(val name: String, val artists: List<Artist>, val rating: Rating?, val lastRatedTs: Long?): SpotifyEvent
}