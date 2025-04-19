package com.example.ratify.core.helper

import com.example.ratify.core.model.FavoritesSortType
import com.example.ratify.core.model.LibrarySortType
import com.example.ratify.core.model.SearchType
import com.example.ratify.core.state.LibraryState
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.ui.navigation.FavoritesNavigationTarget
import com.example.ratify.ui.navigation.LibraryNavigationTarget
import com.example.ratify.ui.navigation.MusicNavigationTarget
import com.example.ratify.ui.navigation.SettingsNavigationTarget
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/* Navigation */
// Controls targets/screens that show up on bottom nav bar (portrait) and nav drawer (landscape)
val NAVIGATION_TARGETS = listOf(
    MusicNavigationTarget,
    LibraryNavigationTarget,
    FavoritesNavigationTarget,
    SettingsNavigationTarget
)
// Starting screen on app launch
val START_NAVIGATION_TARGET = MusicNavigationTarget

/* Library Screen */
// Search options for song list
val LIBRARY_SEARCH_TYPES = listOf(
    SearchType.NAME,
    SearchType.ARTISTS,
    SearchType.ALBUM,
    SearchType.RATING
)
// Sort options for song list
val LIBRARY_SORT_TYPES = listOf(
    LibrarySortType.RATING,
    LibrarySortType.LAST_PLAYED_TS,
    LibrarySortType.LAST_RATED_TS,
    LibrarySortType.TIMES_PLAYED,
    LibrarySortType.NAME
)
// Search bar dropdown options (located under three dots)
fun libraryDropdownOptions(
    songRepository: SongRepository,
    stateRepository: StateRepository,
    libraryState: LibraryState,
    playerState: PlayerState?,
    scope: CoroutineScope
): Pair<List<String>, List<() -> Unit>> {
    val dropdownLabels: List<String> = listOf(
        if (libraryState.visualizerShowing) "Hide visualizer" else "Show visualizer",
        "Delete unrated songs"
    )
    val dropdownOptionOnClick: List<() -> Unit> = listOf(
        { stateRepository.updateVisualizerShowing(!libraryState.visualizerShowing) },
        { scope.launch {
            songRepository.deleteSongsWithNullRating(
                playerState?.track?.name ?: "",
                playerState?.track?.artists ?: listOf()
            )
        } }
    )

    return Pair(dropdownLabels, dropdownOptionOnClick)
}

/* Favorites Screen */
// Search options for grouped song list
val FAVORITES_SEARCH_TYPES = listOf(
    SearchType.ARTISTS,
    SearchType.ALBUM
)
// Sort options for grouped songs
val FAVORITES_SORT_TYPES = listOf(
    FavoritesSortType.RATING,
    FavoritesSortType.NAME,
    FavoritesSortType.NUM_ENTRIES,
    FavoritesSortType.TIMES_PLAYED,
    FavoritesSortType.LAST_RATED_TS,
    FavoritesSortType.LAST_PLAYED_TS
)
// Number of groups per row that show up based on screen orientation
enum class GroupsPerRow(
    val columns: Int
) {
    LANDSCAPE(4),
    PORTRAIT(2)
}
// Highest value that the minEntriesThreshold can go
const val FAVORITES_MAX_SLIDER_VALUE: Long = 30
