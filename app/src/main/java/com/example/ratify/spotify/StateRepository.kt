package com.example.ratify.spotify

import MusicState
import SongRepository
import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.Rating
import com.example.ratify.core.model.SearchType
import com.example.ratify.core.state.FavoritesState
import com.example.ratify.core.state.LibraryState
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

// TODO: find a better location for this class
class StateRepository(
    private val songRepository: SongRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Simple states
    private val _currentRating = MutableStateFlow<Rating?>(null)

    private val _libraryDialog = MutableStateFlow<Song?>(null)
    private val _librarySortType = MutableStateFlow(LibrarySortType.LAST_PLAYED_TS)
    private val _librarySortAscending = MutableStateFlow(false)
    private val _searchType = MutableStateFlow(SearchType.NAME)
    private val _searchQuery = MutableStateFlow("")
    private val _visualizerShowing = MutableStateFlow(false)

    private val _favoritesDialog = MutableStateFlow<GroupedSong?>(null)
    private val _favoritesSortType = MutableStateFlow(FavoritesSortType.RATING)
    private val _favoritesSortAscending = MutableStateFlow(false)
    private val _groupType = MutableStateFlow(GroupType.ARTIST)
    private val _minEntriesThreshold = MutableStateFlow(5)


    // Complex states
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _librarySongs = combine(
        _searchType,
        _searchQuery,
        _librarySortType,
        _librarySortAscending
    ) { searchType, searchQuery, sortType, sortAscending ->
        songRepository.GetLibrarySongs(searchType, searchQuery, sortType, sortAscending)
    }.flatMapLatest { it }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _favoritesSongs = combine(
        _groupType,
        _favoritesSortType,
        _favoritesSortAscending,
        _minEntriesThreshold
    ) { groupType, sortType, sortAscending, minEntriesThreshold ->
        songRepository.GetFavoritesSongs(groupType, sortType, sortAscending, minEntriesThreshold)
    }.flatMapLatest { it }

    private val musicMeta = flowOf(MusicState())
    private val libraryMeta = combine(
        _librarySortType,
        _libraryDialog,
        _searchType,
        _searchQuery,
        _visualizerShowing
    ) { sortType, dialog, searchType, searchQuery, visualizer ->
        LibraryState(
            searchType = searchType,
            librarySortType = sortType,
            libraryDialog = dialog,
            searchQuery = searchQuery,
            visualizerShowing = visualizer
        )
    }
    private val favoritesMeta = combine(
        _favoritesSortType,
        _favoritesSortAscending,
        _favoritesDialog,
        _groupType,
        _minEntriesThreshold
    ) { sortType, sortAsc, dialog, groupType, threshold ->
        FavoritesState(
            favoritesSortType = sortType,
            favoritesDialog = dialog,
            groupType = groupType,
            minEntriesThreshold = threshold
        )
    }


    // Getters
    val musicState = combine(_currentRating, musicMeta) { rating, meta ->
        meta.copy(currentRating = rating)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), MusicState())

    val libraryState = combine(_librarySongs, libraryMeta) { songs, meta ->
        meta.copy(songs = songs)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), LibraryState())

    val favoritesState = combine(_favoritesSongs, favoritesMeta) { songs, meta ->
        meta.copy(groupedSongs = songs)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), FavoritesState())


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