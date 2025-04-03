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
import com.spotify.protocol.types.Artist

@Composable
fun ArtistItem(
    artist: Artist,
    songs: List<Song>,
) {
    val averageRating = songs.map { it.rating?.value ?: 0 }.average()
    val averageRatingInt = averageRating.toInt()
    val ratingColor = getRatingColor(if (averageRatingInt == 0) null else Rating.from(averageRatingInt) )

    GenericItem(
        title = artist.name,
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
val mockArtist = Artist(
    "YSB Tril", unspecifiedString
)

@Preview(name = "Dark Artist Items")
@Composable
fun DarkArtistItemsPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ArtistItem(
                artist = mockArtist,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            ArtistItem(
                artist = mockArtist,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            ArtistItem(
                artist = mockArtist,
                songs = listOf(mockSong, mockSong, mockSong)
            )
        }
    }
}

@Preview(name = "Light Artist Items")
@Composable
fun LightArtistItemsPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ArtistItem(
                artist = mockArtist,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            ArtistItem(
                artist = mockArtist,
                songs = listOf(mockSong, mockSong, mockSong)
            )
            ArtistItem(
                artist = mockArtist,
                songs = listOf(mockSong, mockSong, mockSong)
            )
        }
    }
}