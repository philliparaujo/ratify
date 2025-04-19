package com.example.ratify.repository

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.Rating
import com.example.ratify.core.model.SearchType
import com.example.ratify.core.state.FavoritesState
import com.example.ratify.core.state.LibraryState
import com.example.ratify.core.state.MusicState
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import com.example.ratify.ui.navigation.SnackbarAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StateRepository(
    private val songRepository: SongRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Private states
    private val _musicState = MutableStateFlow(MusicState())
    private val _libraryState = MutableStateFlow(LibraryState())
    private val _favoritesState = MutableStateFlow(FavoritesState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _librarySongs = _libraryState
        .map { Triple(it.searchType, it.searchQuery, it.librarySortType to it.librarySortAscending) }
        .distinctUntilChanged()
        .flatMapLatest { (searchType, query, sort) ->
            songRepository.getLibrarySongs(searchType, query, sort.first, sort.second)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _favoritesSongs = _favoritesState
        .map { Triple(it.groupType, it.favoritesSortType to it.favoritesSortAscending, it.minEntriesThreshold) }
        .distinctUntilChanged()
        .flatMapLatest { (groupType, sort, minEntriesThreshold) ->
            songRepository.getFavoritesSongs(groupType, sort.first, sort.second, minEntriesThreshold)
        }

    // Public states
    val musicState = _musicState
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), MusicState())
    val libraryState = combine(_libraryState, _librarySongs) { state, songs ->
        state.copy(songs = songs)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), LibraryState())
    val favoritesState = combine(_favoritesState, _favoritesSongs) { state, songs ->
        state.copy(groupedSongs = songs)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5000), FavoritesState())

    val snackbarHostState = SnackbarHostState()  // Keeps Snackbars active

    // State setters
    fun updateCurrentRating(rating: Rating?) {
        _musicState.update { it.copy(currentRating = rating) }
    }

    fun updateLibraryDialog(dialog: Song?) {
        _libraryState.update { it.copy(libraryDialog = dialog) }
    }
    fun updateVisualizerShowing(showing: Boolean) {
        _libraryState.update { it.copy(visualizerShowing = showing) }
    }
    fun updateSearchType(type: SearchType) {
        _libraryState.update { it.copy(searchType = type) }
    }
    fun updateSearchQuery(query: String) {
        _libraryState.update { it.copy(searchQuery = query) }
    }
    fun updateLibrarySortType(type: LibrarySortType) {
        if (_libraryState.value.librarySortType == type) {
            updateLibrarySortAscending(!_libraryState.value.librarySortAscending)
        } else {
            updateLibrarySortAscending(type.sortAscendingPreference)
        }

        _libraryState.update { it.copy(librarySortType = type) }
    }
    private fun updateLibrarySortAscending(sortAscending: Boolean) {
        _libraryState.update { it.copy(librarySortAscending = sortAscending) }
    }

    fun updateFavoritesDialog(dialog: GroupedSong?) {
        _favoritesState.update { it.copy(favoritesDialog = dialog) }
    }
    fun updateMinEntriesThreshold(threshold: Int) {
        _favoritesState.update { it.copy(minEntriesThreshold = threshold) }
    }
    fun updateFavoritesSortType(type: FavoritesSortType) {
        if (_favoritesState.value.favoritesSortType == type) {
            updateFavoritesSortAscending(!_favoritesState.value.favoritesSortAscending)
        } else {
            updateFavoritesSortAscending(type.sortAscendingPreference)
        }

        _favoritesState.update { it.copy(favoritesSortType = type) }
    }
    fun updateGroupType(type: GroupType) {
        _favoritesState.update { it.copy(groupType = type) }
    }
    private fun updateFavoritesSortAscending(sortAscending: Boolean) {
        _favoritesState.update { it.copy(favoritesSortAscending = sortAscending) }
    }

    fun showSnackbar(message: String, action: SnackbarAction? = null) {
        snackbarHostState.currentSnackbarData?.dismiss()

        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = action?.name,
                duration = SnackbarDuration.Short
            ).let { result ->
                if (result == SnackbarResult.ActionPerformed) {
                    action?.action?.invoke()
                }
            }
        }
    }
}