package com.example.ratify.mocks

import androidx.sqlite.db.SupportSQLiteQuery
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import com.example.ratify.database.SongDao
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeDao : SongDao {
    private val songs = mutableListOf<Song>()
    private val songsFlow = MutableStateFlow<List<Song>>(emptyList())

    override suspend fun upsertSong(song: Song) {
        songs.removeAll { it.name == song.name && it.artists == song.artists }
        songs.add(song)
        songsFlow.value = songs.toList()
    }

    override suspend fun deleteSong(song: Song) {
        songs.removeAll { it.name == song.name && it.artists == song.artists }
        songsFlow.value = songs.toList()
    }

    override suspend fun deleteSongsWithNullRating(exceptName: String, exceptArtists: List<Artist>): Int {
        val initialSize = songs.size
        songs.removeAll { it.rating == null && (it.name != exceptName || it.artists != exceptArtists) }
        songsFlow.value = songs.toList()
        return initialSize - songs.size
    }

    override suspend fun getSongByPrimaryKey(name: String, artists: List<Artist>): Song? {
        return songs.find { it.name == name && it.artists == artists }
    }

    override fun getSongsByArtist(artist: Artist): Flow<List<Song>> {
        return songsFlow.map { list -> list.filter { it.artists.contains(artist) } }
    }

    override fun getSongsByAlbum(album: Album): Flow<List<Song>> {
        return songsFlow.map { list -> list.filter { it.album == album } }
    }

    override fun querySongs(query: SupportSQLiteQuery): Flow<List<Song>> {
        // For preview/testing purposes, just return all songs
        return songsFlow
    }

    override fun queryGroupedSongs(query: SupportSQLiteQuery): Flow<List<GroupedSong>> {
        // Group songs by first artist for preview/demo purposes
        return songsFlow.map { list ->
            list
                .filter { it.artists.isNotEmpty() }
                .groupBy { it.artists.first() }
                .map { (artist, songsByArtist) ->
                    val count = songsByArtist.size
                    val totalTimesPlayed = songsByArtist.sumOf { it.timesPlayed }
                    val ratings = songsByArtist.mapNotNull { it.rating?.value }
                    val averageRating = if (ratings.isNotEmpty()) ratings.average() else null
                    val lastPlayedTs = songsByArtist.mapNotNull { it.lastPlayedTs }.maxOrNull()
                    val lastRatedTs = songsByArtist.mapNotNull { it.lastRatedTs }.maxOrNull()
                    val album = songsByArtist.firstOrNull()?.album
                    val imageUri = songsByArtist.firstOrNull()?.imageUri

                    GroupedSong(
                        artist = artist,
                        album = album,
                        count = count,
                        totalTimesPlayed = totalTimesPlayed,
                        averageRating = averageRating?.toFloat() ?: 0f,
                        lastPlayedTs = lastPlayedTs ?: 0,
                        lastRatedTs = lastRatedTs ?: 0,
                        imageUri = imageUri
                    )
                }
        }
    }

}
