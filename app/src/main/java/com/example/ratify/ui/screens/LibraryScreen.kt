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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun LibraryScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    val songState = spotifyViewModel?.state?.collectAsState(initial = SongState())?.value ?: SongState()
    val playerState = spotifyViewModel?.playerState?.observeAsState()?.value
    val searchTypes = listOf(SearchType.NAME, SearchType.ARTISTS, SearchType.ALBUM, SearchType.RATING)
    val sortTypes = listOf(SortType.RATING, SortType.LAST_PLAYED_TS, SortType.LAST_RATED_TS, SortType.TIMES_PLAYED, SortType.NAME)

    val userCapabilities = spotifyViewModel?.userCapabilities?.observeAsState()?.value
    val playerEnabled = userCapabilities?.canPlayOnDemand ?: false

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
            onPlay = {
                spotifyViewModel?.onEvent(SpotifyEvent.PlaySong(song.uri))
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
                    onClick = { spotifyViewModel?.onEvent(SpotifyEvent.UpdateShowSongDialog(song)) },
                    onPlay = { spotifyViewModel?.onEvent(SpotifyEvent.PlaySong(song.uri)) },
                    playEnabled = playerEnabled
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
        } else {
            RenderSearch()
            RenderListDetails(modifier = Modifier.fillMaxWidth())
        }

        HorizontalDivider()

        RenderSongList()

        if (songState.visualizerShowing) {
            RenderVisualizer()
        }
        if (songState.currentSongDialog != null) {
            RenderCurrentSongDialog(songState.currentSongDialog)
        }
    }
}

// Previews
@Preview(name = "Library Screen")
@Composable
fun LibraryScreenPreview() {
    RatifyTheme {
        LibraryScreen(
            spotifyViewModel = null
        )
    }
}

@Preview(
    name = "Landscape Library Screen",
    device = landscapeDevice
)
@Composable
fun LandscapeLibraryScreenPreview() {
    RatifyTheme {
        LibraryScreen(
            spotifyViewModel = null
        )
    }
}