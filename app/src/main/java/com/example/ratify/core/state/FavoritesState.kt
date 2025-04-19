package com.example.ratify.core.state

import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.database.GroupedSong

data class FavoritesState(
    val groupedSongs: List<GroupedSong> = emptyList(),
    val groupType: GroupType = GroupType.ARTIST,
    val favoritesSortType: FavoritesSortType = FavoritesSortType.RATING,
    val favoritesSortAscending: Boolean = false,
    val favoritesDialog: GroupedSong? = null,  // Represents currently shown dialog for a GroupedSong
    val minEntriesThreshold: Int = 5,
)
