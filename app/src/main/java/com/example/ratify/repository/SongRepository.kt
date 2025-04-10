package com.example.ratify.repository

import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.Rating
import com.example.ratify.core.model.SearchType
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import com.example.ratify.database.SongDao
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.Track
import kotlinx.coroutines.flow.Flow

class SongRepository(
    val dao: SongDao
) {
    // Fetches
    suspend fun GetSongByPrimaryKey(track: Track): Song? {
        return dao.getSongByPrimaryKey(track.name, track.artists)
    }

    fun GetSongsByGroup(groupType: GroupType, groupName: String, uri: String): Flow<List<Song>> {
        return when (groupType) {
            GroupType.ALBUM -> dao.getSongsByAlbum(Album(groupName, uri))
            GroupType.ARTIST -> dao.getSongsByArtist(Artist(groupName, uri))
        }
    }

    fun GetLibrarySongs(searchType: SearchType?, searchQuery: String?, librarySortType: LibrarySortType?, ascending: Boolean): Flow<List<Song>> {
        return dao.querySongs(
            dao.buildLibraryQuery(
                searchType,
                searchQuery,
                librarySortType,
                ascending
            )
        )
    }

    fun GetFavoritesSongs(groupType: GroupType, favoritesSortType: FavoritesSortType?, ascending: Boolean, minEntriesThreshold: Int = 1): Flow<List<GroupedSong>> {
        return dao.queryGroupedSongs(
            dao.buildFavoritesQuery(
                groupType,
                favoritesSortType,
                ascending,
                minEntriesThreshold
            )
        )
    }

    // Updates
    suspend fun UpsertSong(track: Track, rating: Rating?, lastRatedTs: Long?, lastPlayedTs: Long?, timesPlayed: Int) {
        val song = Song(
            album = track.album,
            artist = track.artist,
            artists = track.artists,
            duration = track.duration,
            imageUri = track.imageUri,
            name = track.name,
            uri = track.uri,
            lastPlayedTs = lastPlayedTs,
            timesPlayed = timesPlayed,
            lastRatedTs = lastRatedTs,
            rating = rating,
        )

        dao.upsertSong(song)
    }

    suspend fun DeleteSong(song: Song) {
        dao.deleteSong(song)
    }

    suspend fun DeleteSongsWithNullRating(exceptName: String, exceptArtists: List<Artist>): Int {
        return dao.deleteSongsWithNullRating(exceptName, exceptArtists)
    }

    suspend fun UpdateLastPlayedTs(name: String, artists: List<Artist>, lastPlayedTs: Long?, timesPlayed: Int) {
        dao.getSongByPrimaryKey(name, artists)?.let {
            dao.upsertSong(it.copy(lastPlayedTs = lastPlayedTs, timesPlayed = timesPlayed))
        }
    }
    suspend fun UpdateRating(name: String, artists: List<Artist>, rating: Rating?, lastRatedTs: Long?) {
        dao.getSongByPrimaryKey(name, artists)?.let {
            dao.upsertSong(it.copy(lastRatedTs = lastRatedTs, rating = rating))
        }
    }
}