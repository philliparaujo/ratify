package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.SearchType
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SongState
import com.example.ratify.spotifydatabase.SortType

@Composable
fun ProfileScreen(
    spotifyViewModel: SpotifyViewModel
) {
    val songState by spotifyViewModel.state.collectAsState(initial = SongState())
    val searchTypes = listOf(SearchType.NAME, SearchType.ARTISTS, SearchType.RATING)
    val sortTypes = listOf(SortType.RATING, SortType.LAST_PLAYED_TS, SortType.LAST_RATED_TS)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Search bar, super delete button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Search...",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            IconButton(
                onClick = {
                    spotifyViewModel.onEvent(SpotifyEvent.DeleteSongsWithNullRating)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete all songs with null rating",
                    tint = Color.LightGray
                )
            }
        }

        // Searching buttons buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Search by:",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            searchTypes.forEach { searchType ->
                Button(
                    enabled = songState.searchType != searchType,
                    onClick = {
                        spotifyViewModel.onEvent(SpotifyEvent.UpdateSearchType(searchType))
                    }
                ) {
                    Text(
                        text = searchType.displayName,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Sorting buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Sort by:",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            sortTypes.forEach { sortType ->
                Button(
                    enabled = songState.sortType != sortType,
                    onClick = {
                        spotifyViewModel.onEvent(SpotifyEvent.UpdateSortType(sortType))
                    }
                ) {
                    Text(
                        text = sortType.displayName,
                        fontSize = 12.sp
                    )
                }
            }
        }

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
                    onDelete = { spotifyViewModel.onEvent(SpotifyEvent.DeleteSong(song)) }
                )
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
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