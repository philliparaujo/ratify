package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.ui.theme.RatifyTheme
import com.spotify.protocol.types.Album

@Composable
fun AlbumItem(
    album: Album,
    songs: List<Song>
) {
    val averageRating = 7.9
    val ratingColor = getRatingColor(Rating.from(averageRating.toInt()))

    GenericItem(
        title = album.name,
        subtitle = "${songs.size} entries",
        ratingColor = ratingColor,
        ratingText = averageRating.toString(),
        displayButton = null,
        onClick = {},
        onLongClick = null,
        imageUri = null
    )
}

// Previews
val mockAlbum = Album(
    "HOTSHOT", unspecifiedString
)

@Preview(name = "Dark Album Items")
@Composable
fun DarkAlbumItemsPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AlbumItem(
                album = mockAlbum,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            AlbumItem(
                album = mockAlbum,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            AlbumItem(
                album = mockAlbum,
                songs = listOf(mockSong, mockSong, mockSong)
            )
        }
    }
}

@Preview(name = "Light Album Items")
@Composable
fun LightAlbumItemsPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AlbumItem(
                album = mockAlbum,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            AlbumItem(
                album = mockAlbum,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            AlbumItem(
                album = mockAlbum,
                songs = listOf(mockSong, mockSong, mockSong)
            )
        }
    }
}