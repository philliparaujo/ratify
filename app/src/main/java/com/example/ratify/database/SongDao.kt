package com.example.ratify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.SearchType
import com.example.ratify.ui.components.Search
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import kotlinx.coroutines.flow.Flow

// Defines all Room DAO operations for querying, inserting, deleting, and filtering songs
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

    @Query("SELECT * FROM songs WHERE artist = :artist")
    fun getSongsByArtist(artist: Artist): Flow<List<Song>>

    @Query("SELECT * FROM songs WHERE album = :album")
    fun getSongsByAlbum(album: Album): Flow<List<Song>>

    @RawQuery(observedEntities = [Song::class])
    fun querySongs(query: SupportSQLiteQuery): Flow<List<Song>>

    @RawQuery(observedEntities = [Song::class])
    fun queryGroupedSongs(query: SupportSQLiteQuery): Flow<List<GroupedSong>>

    fun buildLibraryQuery(
        searchType: SearchType?,
        searchQuery: String?,
        librarySortType: LibrarySortType?,
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
        if (librarySortType != null) {
            baseQuery.append(" ORDER BY ")
            baseQuery.append(
                when (librarySortType) {
                    LibrarySortType.LAST_PLAYED_TS -> "lastPlayedTs"
                    LibrarySortType.LAST_RATED_TS -> "lastRatedTs"
                    LibrarySortType.RATING -> "rating"
                    LibrarySortType.NAME -> "name COLLATE NOCASE"
                    LibrarySortType.TIMES_PLAYED -> "timesPlayed"
                }
            )
            baseQuery.append(if (ascending) " ASC" else " DESC")
        }

        return SimpleSQLiteQuery(baseQuery.toString(), args.toTypedArray())
    }

    fun buildFavoritesQuery(
        searchType: SearchType?,
        searchQuery: String?,
        groupType: GroupType,
        minEntriesThreshold: Int = 1,
        favoritesSortType: FavoritesSortType?,
        ascending: Boolean
    ): SimpleSQLiteQuery {
        val groupColumn = when (groupType) {
            GroupType.ARTIST -> "artist"
            GroupType.ALBUM -> "album"
        }

        val args = mutableListOf<Any>()

        val baseQuery = StringBuilder(
            """
        SELECT 
            songs.artist,
            songs.album,
            grouped.count,
            grouped.averageRating,
            grouped.totalTimesPlayed,
            grouped.lastPlayedTs,
            grouped.lastRatedTs,
            songs.imageUri
        FROM (
            SELECT 
                $groupColumn AS groupName,
                COUNT(*) AS count,
                AVG(rating) AS averageRating,
                SUM(timesPlayed) AS totalTimesPlayed,
                MAX(lastPlayedTs) AS lastPlayedTs,
                MAX(lastRatedTs) AS lastRatedTs
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

        // Optional search filtering
        if (!searchQuery.isNullOrBlank() && searchType == SearchType.ARTISTS) {
            baseQuery.append(" AND songs.artist LIKE ?")
            args.add("%$searchQuery%")
        }
        if (!searchQuery.isNullOrBlank() && searchType == SearchType.ALBUM && groupType == GroupType.ALBUM) {
            baseQuery.append(" AND songs.album LIKE ?")
            args.add("%$searchQuery%")
        }

        // Sorting
        if (favoritesSortType != null) {
            baseQuery.append(" ORDER BY ")
            baseQuery.append(
                when (favoritesSortType) {
                    FavoritesSortType.LAST_PLAYED_TS -> "grouped.lastPlayedTs"
                    FavoritesSortType.LAST_RATED_TS -> "grouped.lastRatedTs"
                    FavoritesSortType.RATING -> "grouped.averageRating"
                    FavoritesSortType.NAME -> "grouped.groupName COLLATE NOCASE"
                    FavoritesSortType.TIMES_PLAYED -> "grouped.totalTimesPlayed"
                    FavoritesSortType.NUM_ENTRIES -> "grouped.count"
                }
            )
            baseQuery.append(if (ascending) " ASC" else " DESC")
        }

        return SimpleSQLiteQuery(baseQuery.toString(), args.toTypedArray())
    }
}