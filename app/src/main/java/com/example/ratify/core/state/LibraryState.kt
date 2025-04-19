package com.example.ratify.core.state

import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.SearchType
import com.example.ratify.database.Song

data class LibraryState(
    val songs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val searchType: SearchType = SearchType.NAME,
    val librarySortType: LibrarySortType = LibrarySortType.LAST_PLAYED_TS,
    val librarySortAscending: Boolean = false,
    val visualizerShowing: Boolean = false,
    val libraryDialog: Song? = null,  // Represents currently shown dialog for a song
)

