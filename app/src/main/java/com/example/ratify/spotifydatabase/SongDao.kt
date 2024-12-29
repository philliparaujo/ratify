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

//    @Query("UPDATE songs SET lastPlayedTs = :lastPlayedTs WHERE uri = :uri")
//    suspend fun updateLastPlayedTs(uri: String, lastPlayedTs: Long?)
//
//    @Query("UPDATE songs SET lastRatedTs = :lastRatedTs, rating = :rating WHERE uri = :uri")
//    suspend fun updateRating(uri: String, lastRatedTs: Long?, rating: Rating?)

    @Query("SELECT * FROM songs WHERE uri = :uri LIMIT 1")
    suspend fun getSongByUri(uri: String): Song?

    @Query("SELECT * FROM songs ORDER BY lastPlayedTs DESC")
    fun getSongsOrderedByLastPlayedTs(): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY lastRatedTs DESC")
    fun getSongsOrderedByLastRatedTs(): Flow<List<Song>>

    @Query("SELECT * FROM songs ORDER BY rating DESC")
    fun getSongsOrderedByRating(): Flow<List<Song>>
}