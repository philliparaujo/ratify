package com.example.ratify.spotifydatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
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

//    @Query("SELECT * FROM songs ORDER BY lastPlayedTs DESC")
//    fun getSongsOrderedByLastPlayedTs(): Flow<List<Song>>
//
//    @Query("SELECT * FROM songs ORDER BY lastRatedTs DESC")
//    fun getSongsOrderedByLastRatedTs(): Flow<List<Song>>
//
//    @Query("SELECT * FROM songs ORDER BY rating DESC")
//    fun getSongsOrderedByRating(): Flow<List<Song>>
//
//    @Query("SELECT * FROM songs WHERE name LIKE '%' || :name || '%'")
//    fun searchByName(name: String): Flow<List<Song>>
//
//    @Query("SELECT * FROM songs WHERE rating = :rating")
//    fun searchByRating(rating: Int): Flow<List<Song>>
//
//    @Query("SELECT * FROM songs WHERE artists LIKE '%' || :artistName || '%'")
//    fun searchByArtistName(artistName: String): Flow<List<Song>>

    @RawQuery(observedEntities = [Song::class])
    fun querySongs(query: SupportSQLiteQuery): Flow<List<Song>>

    fun buildQuery(
        searchType: SearchType?,
        searchQuery: String?,
        sortType: SortType?,
    ): SimpleSQLiteQuery {
        val baseQuery = StringBuilder("SELECT * FROM songs")
        val args = mutableListOf<Any>()

        // Filtering
        if (!searchQuery.isNullOrEmpty() && searchType != null) {
            baseQuery.append( " WHERE ")
            when (searchType) {
                SearchType.NAME -> {
                    baseQuery.append("name LIKE ?")
                    args.add("%$searchQuery%")
                }
                SearchType.ARTISTS -> {
                    baseQuery.append("artists LIKE ?")
                    args.add("%$searchQuery%")
                }
                SearchType.ALBUM -> {
                    baseQuery.append("album LIKE ?")
                    args.add("%$searchQuery%")
                }
                SearchType.RATING -> {
                    baseQuery.append("rating = ?")
                    args.add(searchQuery.toIntOrNull() ?: -1)
                }
            }
        }

        // Sorting
        if (sortType != null) {
            baseQuery.append(" ORDER BY ")
            when (sortType) {
                SortType.LAST_PLAYED_TS -> baseQuery.append("lastPlayedTs DESC")
                SortType.LAST_RATED_TS -> baseQuery.append("lastRatedTs DESC")
                SortType.RATING -> baseQuery.append("rating DESC")
            }
        }

        return SimpleSQLiteQuery(baseQuery.toString(), args.toTypedArray())
    }
}