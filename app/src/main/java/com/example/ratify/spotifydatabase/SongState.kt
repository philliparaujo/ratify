package com.example.ratify.spotifydatabase

data class SongState(
    val songs: List<Song> = emptyList(),
    val sortType: SortType = SortType.LAST_PLAYED_TS,
    val searchType: SearchType = SearchType.NAME,
    val currentRating: Rating? = null,  // Represents song's currently selected rating, shown on UI
)
