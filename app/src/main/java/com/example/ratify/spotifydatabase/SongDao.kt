package com.example.ratify.spotifydatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Upsert
    suspend fun upsertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("DELETE FROM songs WHERE rating IS NULL AND uri != :exceptUri")
    suspend fun deleteSongsWithNullRating(exceptUri: String)

    @Query("SELECT * FROM songs WHERE uri = :uri LIMIT 1")
    suspend fun getSongByUri(uri: String): Song?

    @Query("SELECT * FROM songs ORDER BY lastPlayedTs DESC")
    fun getSongsOrderedByLastPlayedTs(): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY lastRatedTs DESC")
    fun getSongsOrderedByLastRatedTs(): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY rating DESC")
    fun getSongsOrderedByRating(): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE name LIKE '%' || :name || '%'")
    fun searchByName(name: String): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE rating = :rating")
    fun searchByRating(rating: Int): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE artists LIKE '%' || :artistName || '%'")
    fun searchByArtistName(artistName: String): Flow<List<Song>>
}