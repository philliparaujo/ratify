package com.example.ratify.spotifydatabase

// SongState is misleading, better name is AppState
data class MusicState(
    val currentRating: Rating? = null,  // Represents song's currently selected rating, shown on UI
)

data class LibraryState(
    val songs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val searchType: SearchType = SearchType.NAME,
    val librarySortType: LibrarySortType = LibrarySortType.LAST_PLAYED_TS,
    val visualizerShowing: Boolean = false,
    val libraryDialog: Song? = null,  // Represents currently shown dialog for a song
)

data class FavoritesState(
    val groupedSongs: List<GroupedSong> = emptyList(),
    val groupType: GroupType = GroupType.ARTIST,
    val favoritesSortType: FavoritesSortType = FavoritesSortType.RATING,
    val favoritesDialog: GroupedSong? = null,  // Represents currently shown dialog for a GroupedSong
    val minEntriesThreshold: Int = 5,
)
