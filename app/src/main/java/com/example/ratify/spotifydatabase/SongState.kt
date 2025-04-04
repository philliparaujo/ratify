package com.example.ratify.spotifydatabase

// SongState is misleading, better name is AppState
//data class SongState(
//    val songs: List<Song> = emptyList(),
//    val searchType: SearchType = SearchType.NAME,
//    val sortType: SortType = SortType.LAST_PLAYED_TS,
//    val sortAscending: Boolean = false,
//    val searchQuery: String = "",
//    val currentRating: Rating? = null,  // Represents song's currently selected rating, shown on UI
//    val currentSongDialog: Song? = null,  // Represents currently shown dialog for a song
//    val visualizerShowing: Boolean = false,
//)

data class MusicState(
    val currentRating: Rating? = null,  // Represents song's currently selected rating, shown on UI
)

data class LibraryState(
    val songs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val searchType: SearchType = SearchType.NAME,
    val sortType: SortType = SortType.LAST_PLAYED_TS,
    val visualizerShowing: Boolean = false,
    val currentSongDialog: Song? = null,  // Represents currently shown dialog for a song
)

data class FavoritesState(
    val groupedSongs: List<GroupedSong> = emptyList(),
    val groupType: GroupType = GroupType.ARTIST,
    val sortType: SortType = SortType.RATING,
    val minEntriesThreshold: Int = 5,
)
