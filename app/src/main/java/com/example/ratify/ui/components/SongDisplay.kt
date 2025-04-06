package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.core.model.Rating
import com.example.ratify.database.Song
import com.example.ratify.ui.theme.RatifyTheme
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

@Composable
fun SongDisplay(
    title: String,
    artists: String,
    imageUri: String,
    modifier: Modifier = Modifier,
) {
    ImageOverlay(
        imageUri = imageUri,
        renderContent = {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )
                Text(
                    text = artists,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }
        },
        modifier = modifier
    )
}

// Helper functions
fun spotifyUriToImageUrl(spotifyUri: String?): String? {
    val spotifyUriPrefix = "spotify:image:"
    val standardUriPrefix = "https://i.scdn.co/image/"
    return spotifyUri?.replace(spotifyUriPrefix, standardUriPrefix)
}

fun getArtistsString(artists: List<Artist>): String {
    return artists.joinToString(", ") { it.name }
}

// Previews
// Network images don't work with previews
const val unspecifiedString = "foo"
const val songName = "Touchdown (feat. Bankrol Hayden)"
val mockSong = Song(
    album = Album(
        songName,
        unspecifiedString
    ),
    artist = Artist(
        "YSB Tril", unspecifiedString
    ),
    artists = listOf(
        Artist(
            "YSB Tril", unspecifiedString
        ),
        Artist(
            "Bankrol Hayden", unspecifiedString
        )
    ),
    duration = 141581,
    imageUri = ImageUri(
        "spotify:image:ab67616d0000b2739fe3277e1c1295755de75305"
    ),
    name = songName,
    uri = "spotify:track:7xWnMfIVVnI3Y3zPp9Ukvi",
    lastPlayedTs = 1737132383109,
    timesPlayed = 1,
    lastRatedTs = 1737132383109,
    rating = Rating.from(10)
)
const val longSongName = "Symphony No. 40 in G Minor, K. 550: I.Allegro molto"
val longMockSong = Song(
    album = Album(
        longSongName,
        unspecifiedString
    ),
    artist = Artist(
        "Wolfgang Amadeus Mozart", unspecifiedString
    ),
    artists = listOf(
        Artist(
            "Wolfgang Amadeus Mozart", unspecifiedString
        ),
        Artist(
            "Capella Instropolitana", unspecifiedString
        ),
        Artist(
            "Barry Wordsworth", unspecifiedString
        )
    ),
    duration = 451333,
    imageUri = ImageUri(
        unspecifiedString
    ),
    name = longSongName,
    uri = unspecifiedString,
    lastPlayedTs = 1737132383109,
    timesPlayed = 1,
    lastRatedTs = 1737132383109,
    rating = Rating.from(10)
)

@Preview(name = "Dark Song Display")
@Composable
fun DarkSongDisplayPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SongDisplay(
                title = mockSong.name,
                artists = getArtistsString(mockSong.artists),
                imageUri = mockSong.imageUri.raw ?: ""
            )
        }
    }
}

@Preview(name = "Light Song Display")
@Composable
fun LightSongDisplayPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SongDisplay(
                title = mockSong.name,
                artists = getArtistsString(mockSong.artists),
                imageUri = mockSong.imageUri.raw ?: ""
            )
        }
    }
}

@Preview(name = "Dark Long Name Song Display")
@Composable
fun DarkLongNameSongDisplayPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SongDisplay(
                title = longMockSong.name,
                artists = getArtistsString(longMockSong.artists),
                imageUri = longMockSong.imageUri.raw ?: ""
            )
        }
    }
}

@Preview(name = "Light Long Name Song Display")
@Composable
fun LightLongNameSongDisplayPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SongDisplay(
                title = longMockSong.name,
                artists = getArtistsString(longMockSong.artists),
                imageUri = longMockSong.imageUri.raw ?: ""
            )
        }
    }
}
