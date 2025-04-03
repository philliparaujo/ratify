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
import com.spotify.protocol.types.ImageUri
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ArtistItem(
    name: String,
    songCount: Int,
    averageRating: Float,
    imageUri: ImageUri?
) {
    val averageRatingInt = averageRating.toInt()
    val ratingColor = getRatingColor(if (averageRatingInt == 0) null else Rating.from(averageRatingInt) )

    GenericItem(
        title = name,
        subtitle = "${songCount} entries",
        ratingColor = ratingColor,
        ratingText = averageRating.roundTo(1).toString(),
        displayButton = null,
        onClick = {},
        onLongClick = null,
        imageUri = imageUri
    )
}

fun Float.roundTo(n: Int): Float {
    val factor = 10.0.pow(n)
    return (this * factor).roundToInt() / factor.toFloat()
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
//            ArtistItem(
//                artist = mockArtist,
//                songs = listOf(mockSong, mockSong, mockSong)
//            )
//            ArtistItem(
//                artist = mockArtist,
//                songs = listOf(mockSong, mockSong, mockSong)
//            )
//            ArtistItem(
//                artist = mockArtist,
//                songs = listOf(mockSong, mockSong, mockSong)
//            )
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
//            ArtistItem(
//                artist = mockArtist,
//                songs = listOf(mockSong, mockSong, mockSong)
//            )
//            ArtistItem(
//                artist = mockArtist,
//                songs = listOf(mockSong, mockSong, mockSong)
//            )
//            ArtistItem(
//                artist = mockArtist,
//                songs = listOf(mockSong, mockSong, mockSong)
//            )
        }
    }
}