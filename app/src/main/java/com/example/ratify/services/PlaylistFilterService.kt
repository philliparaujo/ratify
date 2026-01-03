package com.example.ratify.services

import com.example.ratify.core.model.PlaylistCreationConfig
import com.example.ratify.core.model.SearchField
import com.example.ratify.repository.SongRepository
import kotlinx.coroutines.flow.first

class PlaylistFilterService(
    private val songRepository: SongRepository
) {
    /**
     * Filters songs from the library based on the provided configuration
     * and returns a list of Spotify URIs to add to the playlist.
     *
     * @param config The playlist creation configuration
     * @return List of Spotify track URIs
     */
    suspend fun getFilteredSongUris(config: PlaylistCreationConfig): List<String> {
        // Get all songs from the library (pass null for all songs, no sorting needed)
        val allSongs = songRepository.getLibrarySongs(
            searchType = null,
            searchQuery = null,
            librarySortType = null,
            ascending = true
        ).first()

        // Filter by search query
        val filteredSongs = if (config.searchQuery.isBlank()) {
            allSongs
        } else {
            allSongs.filter { song ->
                when (config.searchField) {
                    SearchField.ARTIST -> song.artists.any { artist ->
                        artist.name.contains(config.searchQuery, ignoreCase = true)
                    }
                    SearchField.ALBUM -> song.album.name.contains(config.searchQuery, ignoreCase = true)
                    SearchField.SONG_NAME -> song.name.contains(config.searchQuery, ignoreCase = true)
                }
            }
        }

        // Apply rating mapping to create the final list
        val playlistUris = mutableListOf<String>()

        for (song in filteredSongs) {
            val rating = song.rating?.value ?: continue // Skip songs without ratings
            val entryCount = config.ratingMap[rating] ?: 0

            // Add the song URI multiple times based on the rating map
            repeat(entryCount) {
                playlistUris.add(song.uri)
            }
        }

        return playlistUris
    }

    /**
     * Get the count of songs that match the filter criteria
     * Useful for preview before creating playlist
     */
    suspend fun getFilteredSongCount(config: PlaylistCreationConfig): Int {
        // Get all songs from the library (pass null for all songs, no sorting needed)
        val allSongs = songRepository.getLibrarySongs(
            searchType = null,
            searchQuery = null,
            librarySortType = null,
            ascending = true
        ).first()

        val filteredSongs = if (config.searchQuery.isBlank()) {
            allSongs
        } else {
            allSongs.filter { song ->
                when (config.searchField) {
                    SearchField.ARTIST -> song.artists.any { artist ->
                        artist.name.contains(config.searchQuery, ignoreCase = true)
                    }
                    SearchField.ALBUM -> song.album.name.contains(config.searchQuery, ignoreCase = true)
                    SearchField.SONG_NAME -> song.name.contains(config.searchQuery, ignoreCase = true)
                }
            }
        }

        // Count total entries considering rating map
        var totalEntries = 0
        for (song in filteredSongs) {
            val rating = song.rating?.value ?: continue
            val entryCount = config.ratingMap[rating] ?: 0
            totalEntries += entryCount
        }

        return totalEntries
    }
}
