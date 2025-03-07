package com.example.ratify.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.SearchType
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SongState
import com.example.ratify.spotifydatabase.SortType
import com.example.ratify.ui.components.Dialog
import com.example.ratify.ui.components.DropdownSelect
import com.example.ratify.ui.components.Search
import com.example.ratify.ui.components.SongItem
import com.example.ratify.ui.components.Visualizer
import com.example.ratify.ui.navigation.LibraryNavigationTarget
import com.example.ratify.ui.navigation.isRouteOnTarget
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun LibraryScreen(
    spotifyViewModel: SpotifyViewModel?,
    navController: NavController
) {
    // Current states (UI and Spotify Player)
    val songState = spotifyViewModel?.state?.collectAsState(initial = SongState())?.value ?: SongState()
    val playerState = spotifyViewModel?.playerState?.observeAsState()?.value

    // Active search/sort options
    val searchTypes = listOf(SearchType.NAME, SearchType.ARTISTS, SearchType.ALBUM, SearchType.RATING)
    val sortTypes = listOf(SortType.RATING, SortType.LAST_PLAYED_TS, SortType.LAST_RATED_TS, SortType.TIMES_PLAYED, SortType.NAME)

    // Player enabled logic
    val userCapabilities = spotifyViewModel?.userCapabilities?.observeAsState()?.value
    val playerEnabled = userCapabilities?.canPlayOnDemand ?: false

    // Orientation logic
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Figure out which Target is currently selected
    // Relies on composable<Target> route being a substring of Target.toString()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Prevents song items from being interactable when navigating away from screen
    val isScreenActive = isRouteOnTarget(currentRoute, LibraryNavigationTarget)

    // Settings variables
    val settings = spotifyViewModel?.settings
    val showImageUri = settings?.libraryImageUri?.collectAsState(true)
    val queueSkip = settings?.queueSkip?.collectAsState(false)
    fun realPlay(song: Song) {
        if (queueSkip?.value == true) {
            spotifyViewModel.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
            Thread.sleep(1000)
            spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
        } else {
            spotifyViewModel?.onEvent(SpotifyEvent.PlaySong(song.uri, song.name))
        }
    }

    @Composable
    fun RenderVisualizer() {
        Visualizer(
            heights = (1..10).map { rating ->
                songState.songs.count {
                        song -> song.rating?.value == rating }.toFloat()
            }
        )
    }

    @Composable
    fun RenderCurrentSongDialog(song: Song) {
        AnimatedVisibility(
            visible = true,
        ) { }
        Dialog(
            onDismissRequest = {
                spotifyViewModel?.onEvent(SpotifyEvent.UpdateShowSongDialog(null))
            },
            song = song,
            onRatingSelect = { rating ->
                // Update current rating (UI indicator)
                val ratingValue = Rating.from(rating)
                if (playerState?.track?.uri == song.uri) {
                    spotifyViewModel.onEvent(SpotifyEvent.UpdateCurrentRating(ratingValue))
                }
                // Update rating in database
                spotifyViewModel?.onEvent(
                    SpotifyEvent.UpdateRating(
                        name = song.name,
                        artists = song.artists,
                        rating = ratingValue,
                        lastRatedTs = System.currentTimeMillis()
                    )
                )
            },
            onPlay = { realPlay(song) },
            onDisabledPlay = {
                spotifyViewModel?.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
            },
            onDelete = {
                spotifyViewModel?.onEvent(SpotifyEvent.DeleteSong(song))
            },
            playEnabled = playerEnabled,
            deleteEnabled = true
        )
    }

    @Composable
    fun RenderSongList() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(songState.songs) { song ->
                SongItem(
                    song = song,
                    onClick = {
                        if (isScreenActive) spotifyViewModel?.onEvent(SpotifyEvent.UpdateShowSongDialog(song))
                    },
                    onLongClick = {
                        if (isScreenActive) {
                            if (playerEnabled) {
                                spotifyViewModel?.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
                            } else {
                                spotifyViewModel?.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
                            }
                        }
                    },
                    onPlay = { if (isScreenActive) realPlay(song) },
                    onDisabledPlay = {
                        if (isScreenActive) spotifyViewModel?.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
                    },
                    playEnabled = playerEnabled,
                    showImageUri = showImageUri?.value ?: true
                )
            }
        }
    }

    @Composable
    fun RenderSearch() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Search(
                query = songState.searchQuery,
                onQueryChange = { spotifyViewModel?.onSearchTextChange(it) },
                placeholderText = "Search",
                trailingIcon = Icons.Default.MoreVert,
                dropdownLabels = listOf(
                    if (songState.visualizerShowing) "Hide visualizer" else "Show visualizer",
                    "Delete unrated songs"
                ),
                dropdownOptionOnClick = listOf(
                    { spotifyViewModel?.onEvent(SpotifyEvent.UpdateVisualizerShowing(!songState.visualizerShowing)) },
                    { spotifyViewModel?.onEvent(SpotifyEvent.DeleteSongsWithNullRating(
                        playerState?.track?.name ?: "",
                        playerState?.track?.artists ?: listOf())) }
                ),
                modifier = Modifier.weight(1f)
            )

            // Searching dropdown
            DropdownSelect(
                options = searchTypes,
                selectedOption = songState.searchType,
                onSelect = { searchType -> spotifyViewModel?.onEvent(SpotifyEvent.UpdateSearchType(searchType)) },
                label = "Search by",
                modifier = Modifier.wrapContentWidth()
            )
        }
    }

    @Composable
    fun RenderListDetails(modifier: Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "${songState.songs.count()} entries",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
                val totalRatings = songState.songs.mapNotNull { song -> song.rating?.value }
                val averageScore = if (totalRatings.isNotEmpty()) {
                    totalRatings.sum().toFloat() / totalRatings.size
                } else {
                    0f
                }
                Text(
                    "Average score: %.1f".format(averageScore),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
            }

            // Sorting dropdown
            DropdownSelect(
                options = sortTypes,
                selectedOption = songState.sortType,
                onSelect = { sortType -> spotifyViewModel?.onEvent(SpotifyEvent.UpdateSortType(sortType)) },
                label = "Sort by",
                large = true
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(4f)
                ) {
                    RenderSearch()
                }

                RenderListDetails(modifier = Modifier.weight(3f))
            }
            if (songState.visualizerShowing) {
                RenderVisualizer()
            }
        } else {
            RenderSearch()
            if (songState.visualizerShowing) {
                RenderVisualizer()
            }
            RenderListDetails(modifier = Modifier.fillMaxWidth())
        }

        HorizontalDivider()

        RenderSongList()
        if (songState.currentSongDialog != null) {
            RenderCurrentSongDialog(songState.currentSongDialog)
        }
    }
}

// Previews
@Preview(name = "Dark Library Screen")
@Composable
fun DarkLibraryScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        LibraryScreen(
            spotifyViewModel = null,
            navController = rememberNavController()
        )
    }
}

@Preview(name = "Light Library Screen")
@Composable
fun LightLibraryScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        LibraryScreen(
            spotifyViewModel = null,
            navController = rememberNavController()
        )
    }
}

@Preview(
    name = "Dark Landscape Library Screen",
    device = landscapeDevice
)
@Composable
fun DarkLandscapeLibraryScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        LibraryScreen(
            spotifyViewModel = null,
            navController = rememberNavController()
        )
    }
}

@Preview(
    name = "Light Landscape Library Screen",
    device = landscapeDevice
)
@Composable
fun LightLandscapeLibraryScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        LibraryScreen(
            spotifyViewModel = null,
            navController = rememberNavController()
        )
    }
}