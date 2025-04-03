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

    @RawQuery(observedEntities = [Song::class])
    fun queryGroupedSongs(query: SupportSQLiteQuery): Flow<List<GroupedSong>>

    fun buildLibraryQuery(
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

    fun buildFavoritesQuery(
        groupType: GroupType,
        sortType: SortType?,
        ascending: Boolean,
        minEntriesThreshold: Int = 1,
    ): SimpleSQLiteQuery {
        val groupColumn = when (groupType) {
            GroupType.ARTIST -> "artist"
            GroupType.ALBUM -> "album"
        }

        val baseQuery = StringBuilder(
            """
        SELECT 
            songs.artist,
            songs.album,
            grouped.count,
            grouped.averageRating,
            songs.imageUri
        FROM (
            SELECT 
                $groupColumn AS groupName,
                COUNT(*) AS count,
                AVG(rating) AS averageRating
            FROM songs
            WHERE rating IS NOT NULL
            GROUP BY $groupColumn
            HAVING COUNT(*) >= $minEntriesThreshold
        ) AS grouped
        JOIN songs ON songs.$groupColumn = grouped.groupName
        WHERE songs.rowid = (
            SELECT rowid FROM songs AS s
            WHERE s.$groupColumn = grouped.groupName
              AND s.rating IS NOT NULL
            ORDER BY 
                s.rating DESC,
                s.timesPlayed DESC,
                s.lastPlayedTs DESC
            LIMIT 1
        )
        """.trimIndent()
        )

        // Sorting
        if (sortType != null) {
            baseQuery.append(" ORDER BY ")
            baseQuery.append(
                when (sortType) {
                    SortType.LAST_PLAYED_TS -> "songs.lastPlayedTs"
                    SortType.LAST_RATED_TS -> "songs.lastRatedTs"
                    SortType.RATING -> "grouped.averageRating"
                    SortType.NAME -> "songs.$groupColumn COLLATE NOCASE"
                    SortType.TIMES_PLAYED -> "songs.timesPlayed"
                }
            )
            baseQuery.append(if (ascending) " ASC" else " DESC")
        }

        return SimpleSQLiteQuery(baseQuery.toString())
    }
}