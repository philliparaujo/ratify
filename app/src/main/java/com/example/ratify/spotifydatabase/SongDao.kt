package com.example.ratify.spotifydatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.spotify.protocol.types.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Upsert
    suspend fun upsertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("DELETE FROM songs WHERE rating IS NULL AND NOT (name = :exceptName AND artists = :exceptArtists)")
    suspend fun deleteSongsWithNullRating(exceptName: String, exceptArtists: List<Artist>): Int

    @Query("SELECT * FROM songs WHERE name = :name AND artists = :artists LIMIT 1")
    suspend fun getSongByPrimaryKey(name: String, artists: List<Artist>): Song?

    @RawQuery(observedEntities = [Song::class])
    fun querySongs(query: SupportSQLiteQuery): Flow<List<Song>>

    fun buildQuery(
        searchType: SearchType?,
        searchQuery: String?,
        sortType: SortType?,
        ascending: Boolean,
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
            baseQuery.append(
                when (sortType) {
                    SortType.LAST_PLAYED_TS -> "lastPlayedTs"
                    SortType.LAST_RATED_TS -> "lastRatedTs"
                    SortType.RATING -> "rating"
                    SortType.NAME -> "name COLLATE NOCASE"
                    SortType.TIMES_PLAYED -> "timesPlayed"
                }
            )
            baseQuery.append(if (ascending) " ASC" else " DESC")
        }

        return SimpleSQLiteQuery(baseQuery.toString(), args.toTypedArray())
    }
}