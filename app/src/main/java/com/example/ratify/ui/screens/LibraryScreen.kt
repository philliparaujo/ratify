package com.example.ratify.ui.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ratify.core.helper.LIBRARY_SEARCH_TYPES
import com.example.ratify.core.helper.LIBRARY_SORT_TYPES
import com.example.ratify.core.helper.isLandscapeOrientation
import com.example.ratify.core.helper.libraryDropdownOptions
import com.example.ratify.core.model.Rating
import com.example.ratify.core.state.LibraryState
import com.example.ratify.database.Song
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.services.updateRatingService
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.components.DropdownSelect
import com.example.ratify.ui.components.Search
import com.example.ratify.ui.components.SongDialog
import com.example.ratify.ui.components.SongItem
import com.example.ratify.ui.components.Visualizer
import com.example.ratify.ui.navigation.LibraryNavigationTarget
import com.example.ratify.ui.navigation.isRouteOnTarget
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    navController: NavHostController
) {
    val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current
    val songRepository: SongRepository = LocalSongRepository.current
    val stateRepository: StateRepository = LocalStateRepository.current
    val settingsRepository: SettingsRepository = LocalSettingsRepository.current

    // Current states (UI and Spotify Player)
    val libraryState = stateRepository.libraryState.collectAsState(initial = LibraryState()).value
    val playerState by spotifyViewModel.playerState.collectAsState()

    // Player enabled logic
    val userCapabilities = spotifyViewModel.userCapabilities.observeAsState().value
    val playerEnabled = userCapabilities?.canPlayOnDemand ?: false

    // Prevents song items from being interactable when navigating away from screen
    val isScreenActive = isRouteOnTarget(navController, LibraryNavigationTarget)

    // Settings variables
    val showImageUri = settingsRepository.libraryImageUri.collectAsState(true)
    val queueSkip = settingsRepository.queueSkip.collectAsState(false)

    // Handles up-to-date search query
    var localTextFieldValue by remember { mutableStateOf(TextFieldValue(libraryState.searchQuery)) }
    LaunchedEffect(libraryState.searchQuery) {
        if (libraryState.searchQuery != localTextFieldValue.text) {
            localTextFieldValue = TextFieldValue(
                text = libraryState.searchQuery,
                selection = TextRange(libraryState.searchQuery.length)
            )
        }
    }

    @Composable
    fun RenderVisualizer() {
        Visualizer(
            heights = (1..10).map { rating ->
                libraryState.songs.count {
                        song -> song.rating?.value == rating }.toFloat()
            }
        )
    }

    @Composable
    fun RenderCurrentSongDialog(song: Song) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        SongDialog(
            onDismissRequest = {
                stateRepository.updateLibraryDialog(null)
            },
            song = song,
            onRatingSelect = { rating ->
                // Update current rating (UI indicator)
                val ratingValue = Rating.from(rating)
                if (playerState?.track?.uri == song.uri) {
                    stateRepository.updateCurrentRating(ratingValue)
                    context.updateRatingService(ratingValue)
                }

                // Update rating in database
                scope.launch {
                    songRepository.updateRating(
                        name = song.name,
                        artists = song.artists,
                        rating = ratingValue,
                        lastRatedTs = System.currentTimeMillis()
                    )
                }
            },
            onPlay = {
                spotifyViewModel.onEvent(SpotifyEvent.PlaySong(song.uri, song.name, queueSkip.value))
            },
            onDisabledPlay = {
                spotifyViewModel.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
            },
            onDelete = {
                scope.launch {
                    songRepository.deleteSong(song)
                }
            },
            playEnabled = playerEnabled,
            deleteEnabled = true
        )
    }

    @Composable
    fun RenderSongList() {
        if (libraryState.songs.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No songs in your library",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (libraryState.searchQuery == "") {
                        "When you play a song it will be added here"
                    } else {
                        "Listen to more songs or adjust the search criteria"
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(libraryState.songs) { song ->
                    SongItem(
                        song = song,
                        onClick = {
                            if (isScreenActive) stateRepository.updateLibraryDialog(song)
                        },
                        onLongClick = {
                            if (isScreenActive) {
                                if (playerEnabled) {
                                    spotifyViewModel.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
                                } else {
                                    spotifyViewModel.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
                                }
                            }
                        },
                        onPlay = { if (isScreenActive)
                            spotifyViewModel.onEvent(SpotifyEvent.PlaySong(song.uri, song.name, queueSkip.value))
                        },
                        onDisabledPlay = {
                            if (isScreenActive) spotifyViewModel.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
                        },
                        playEnabled = playerEnabled,
                        showImageUri = showImageUri.value
                    )
                }
            }
        }
    }

    @Composable
    fun RenderSearch() {
        val scope = rememberCoroutineScope()
        val (dropdownLabels, dropdownOptionOnClick) = libraryDropdownOptions(
            songRepository, stateRepository, libraryState, playerState, scope
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Search(
                query = localTextFieldValue.text,
                onQueryChange = {
                    localTextFieldValue = localTextFieldValue.copy(text = it, selection = TextRange(it.length))
                    stateRepository.updateLibrarySearchQuery(it)
                },
                placeholderText = "Search",
                trailingIcon = Icons.Default.MoreVert,
                dropdownLabels = dropdownLabels,
                dropdownOptionOnClick = dropdownOptionOnClick,
                modifier = Modifier.weight(1f)
            )

            // Searching dropdown
            DropdownSelect(
                options = LIBRARY_SEARCH_TYPES,
                selectedOption = libraryState.searchType,
                onSelect = { searchType -> stateRepository.updateLibrarySearchType(searchType) },
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
                    "${libraryState.songs.count()} entries",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
                val totalRatings = libraryState.songs.mapNotNull { song -> song.rating?.value }
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
                options = LIBRARY_SORT_TYPES,
                selectedOption = libraryState.sortType,
                onSelect = { stateRepository.updateLibrarySortType(it) },
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
        if (isLandscapeOrientation()) {
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
            if (libraryState.visualizerShowing) {
                RenderVisualizer()
            }
        } else {
            RenderSearch()
            if (libraryState.visualizerShowing) {
                RenderVisualizer()
            }
            RenderListDetails(modifier = Modifier.fillMaxWidth())
        }

        HorizontalDivider()

        RenderSongList()
        if (libraryState.dialog != null) {
            RenderCurrentSongDialog(libraryState.dialog)
        }
    }
}

// Previews
@PreviewSuite
@Composable
fun LibraryScreenPreviews() {
    MyPreview {
        LibraryScreen(
            navController = rememberNavController()
        )
    }
}