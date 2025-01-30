package com.example.ratify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.ui.theme.RatifyTheme

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
@Preview(name = "Song Item")
@Composable
fun SongItemPreview() {
    RatifyTheme(darkTheme = true) {
        SongItem(
            song = mockSong,
            onClick = { },
            onDelete = { }
        )
    }
}