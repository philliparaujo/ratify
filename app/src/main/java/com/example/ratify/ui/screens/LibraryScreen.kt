package com.example.ratify.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun LibraryScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    val songState = spotifyViewModel?.state?.collectAsState(initial = SongState())?.value ?: SongState()
    val playerState = spotifyViewModel?.playerState?.observeAsState()?.value
    val searchTypes = listOf(SearchType.NAME, SearchType.ARTISTS, SearchType.ALBUM, SearchType.RATING)
    val sortTypes = listOf(SortType.RATING, SortType.LAST_PLAYED_TS, SortType.LAST_RATED_TS)

    val userCapabilities = spotifyViewModel?.userCapabilities?.observeAsState()?.value
    val playerEnabled = userCapabilities?.canPlayOnDemand ?: false

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Search bar, dropdown select,  *super delete button
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Search(
                query = songState.searchQuery,
                onQueryChange = { spotifyViewModel?.onSearchTextChange(it) },
                placeholderText = "Search",
                trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = "More search options") },
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

// SUPER DELETE BUTTON
//            IconButton(
//                onClick = {
//                    spotifyViewModel.onEvent(SpotifyEvent.DeleteSongsWithNullRating(
//                        playerState?.track?.uri ?: ""))
//                }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Delete all songs with null rating",
//                    tint = Color.LightGray
//                )
//            }
        }

        // Sorting dropdown
        DropdownSelect(
            options = sortTypes,
            selectedOption = songState.sortType,
            onSelect = { sortType -> spotifyViewModel?.onEvent(SpotifyEvent.UpdateSortType(sortType)) },
            label = "Sort by",
            large = true
        )

        HorizontalDivider()

        // Song list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(songState.songs) { song ->
                SongItem(
                    song = song,
                    onClick = { spotifyViewModel?.onEvent(SpotifyEvent.UpdateShowSongDialog(song)) },
                    onDelete = { spotifyViewModel?.onEvent(SpotifyEvent.DeleteSong(song)) }
                )
            }
        }

        // Song dialog
        if (songState.currentSongDialog != null) {
            Dialog(
                onDismissRequest = {
                    spotifyViewModel?.onEvent(SpotifyEvent.UpdateShowSongDialog(null))
                },
                song = songState.currentSongDialog,
                onRatingSelect = { rating ->
                    // Update current rating (UI indicator)
                    val ratingValue = Rating.from(rating)
                    if (playerState?.track?.uri == songState.currentSongDialog.uri) {
                        spotifyViewModel.onEvent(SpotifyEvent.UpdateCurrentRating(ratingValue))
                    }

                    // Update rating in database
                    spotifyViewModel?.onEvent(SpotifyEvent.UpdateRating(
                        uri = songState.currentSongDialog.uri,
                        rating = ratingValue,
                        lastRatedTs = System.currentTimeMillis()
                    ))
                },
                onPlay = {
                    spotifyViewModel?.onEvent(SpotifyEvent.PlaySong(songState.currentSongDialog.uri))
                },
                onDelete = {
                    spotifyViewModel?.onEvent(SpotifyEvent.DeleteSong(songState.currentSongDialog))
                },
                playEnabled = playerEnabled,
                deleteEnabled = true
            )
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Fixed-width container for the rating text
        Box(
            modifier = Modifier
                .width(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (song.rating != null) "(" + song.rating.value.toString() + ")" else "-",
                fontSize = 12.sp
            )
        }

        // Song name and artist
        Text(
            text = song.name + " - " + song.artist.name.toString(),
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Delete button
        IconButton(
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete song"
            )
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