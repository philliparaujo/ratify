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
import com.example.ratify.mocks.Preview
import com.example.ratify.mocks.longMockSong
import com.example.ratify.mocks.mockSong
import com.spotify.protocol.types.Artist

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
@Preview(name = "Dark Song Display")
@Composable
fun DarkSongDisplayPreview() {
    Preview(darkTheme = true) {
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
    Preview(darkTheme = false) {
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
    Preview(darkTheme = true) {
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
    Preview(darkTheme = false) {
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
