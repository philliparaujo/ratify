package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SongState

@Composable
fun ProfileScreen(
    spotifyViewModel: SpotifyViewModel
) {
    val songState by spotifyViewModel.state.collectAsState(initial = SongState())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
                .width(20.dp),
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