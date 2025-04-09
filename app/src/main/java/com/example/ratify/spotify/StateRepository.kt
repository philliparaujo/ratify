package com.example.ratify.spotify

import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.Rating
import com.example.ratify.core.model.SearchType
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: find a better location for this class
class StateRepository {
    // State variables
    private val _currentRating = MutableStateFlow<Rating?>(null)
    private val _libraryDialog = MutableStateFlow<Song?>(null)
    private val _favoritesDialog = MutableStateFlow<GroupedSong?>(null)
    private val _visualizerShowing = MutableStateFlow(false)
    private val _minEntriesThreshold = MutableStateFlow(5)
    private val _searchType = MutableStateFlow(SearchType.NAME)
    private val _searchQuery = MutableStateFlow("")
    private val _librarySortType = MutableStateFlow(LibrarySortType.LAST_PLAYED_TS)
    private val _favoritesSortType = MutableStateFlow(FavoritesSortType.RATING)
    private val _groupType = MutableStateFlow(GroupType.ARTIST)
    private val _librarySortAscending = MutableStateFlow(false)
    private val _favoritesSortAscending = MutableStateFlow(false)

    // Getters
    val currentRating: StateFlow<Rating?> = _currentRating
    val libraryDialog: StateFlow<Song?> = _libraryDialog
    val favoritesDialog: StateFlow<GroupedSong?> = _favoritesDialog
    val visualizerShowing: StateFlow<Boolean> = _visualizerShowing
    val minEntriesThreshold: StateFlow<Int> = _minEntriesThreshold
    val searchType: StateFlow<SearchType> = _searchType
    val searchQuery: StateFlow<String> = _searchQuery
    val librarySortType: StateFlow<LibrarySortType> = _librarySortType
    val favoritesSortType: StateFlow<FavoritesSortType> = _favoritesSortType
    val groupType: StateFlow<GroupType> = _groupType
    val librarySortAscending: StateFlow<Boolean> = _librarySortAscending
    val favoritesSortAscending: StateFlow<Boolean> = _favoritesSortAscending


    // Setters
    fun updateCurrentRating(rating: Rating?) {
        _currentRating.value = rating
    }
    fun updateLibraryDialog(dialog: Song?) {
        _libraryDialog.value = dialog
    }
    fun updateFavoritesDialog(dialog: GroupedSong?) {
        _favoritesDialog.value = dialog
    }
    fun updateVisualizerShowing(showing: Boolean) {
        _visualizerShowing.value = showing
    }
    fun updateMinEntriesThreshold(threshold: Int) {
        _minEntriesThreshold.value = threshold
    }
    fun updateSearchType(type: SearchType) {
        _searchType.value = type
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun updateLibrarySortType(type: LibrarySortType) {
        if (_librarySortType.value == type) {
            updateLibrarySortAscending(!_librarySortAscending.value)
        } else {
            updateLibrarySortAscending(type.sortAscendingPreference)
        }

        _librarySortType.value = type
    }
    fun updateFavoritesSortType(type: FavoritesSortType) {
        if (_favoritesSortType.value == type) {
            updateFavoritesSortAscending(!_favoritesSortAscending.value)
        } else {
            updateFavoritesSortAscending(type.sortAscendingPreference)
        }

        _favoritesSortType.value = type
    }
    fun updateGroupType(type: GroupType) {
        _groupType.value = type
    }
    fun updateLibrarySortAscending(sortAscending: Boolean) {
        _librarySortAscending.value = sortAscending
    }
    fun updateFavoritesSortAscending(sortAscending: Boolean) {
        _favoritesSortAscending.value = sortAscending
    }
}