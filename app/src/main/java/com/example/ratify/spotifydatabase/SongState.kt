package com.example.ratify.spotifydatabase

data class SongState(
    val songs: List<Song> = emptyList(),
    val sortType: SortType = SortType.LAST_PLAYED_TS
)
