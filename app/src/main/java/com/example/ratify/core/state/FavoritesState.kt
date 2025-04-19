package com.example.ratify.core.state

import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.SearchType
import com.example.ratify.database.GroupedSong

data class FavoritesState(
    val groupedSongs: List<GroupedSong> = emptyList(),

    val groupType: GroupType = GroupType.ARTIST,
    val minEntriesThreshold: Int = 5,

    val searchQuery: String = "",
    val searchType: SearchType = SearchType.ARTISTS,

    val sortType: FavoritesSortType = FavoritesSortType.RATING,
    val sortAscending: Boolean = false,

    val dialog: GroupedSong? = null,  // Represents currently shown dialog for a GroupedSong
)
