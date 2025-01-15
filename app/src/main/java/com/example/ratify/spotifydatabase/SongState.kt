package com.example.ratify.spotifydatabase

// SongState is misleading, better name is AppState
data class SongState(
    val songs: List<Song> = emptyList(),
    val sortType: SortType = SortType.LAST_PLAYED_TS,
    val searchType: SearchType = SearchType.NAME,
    val searchQuery: String = "",
    val currentRating: Rating? = null,  // Represents song's currently selected rating, shown on UI
    val currentSongDialog: Song? = null,  // Represents currently shown dialog for a song
)
