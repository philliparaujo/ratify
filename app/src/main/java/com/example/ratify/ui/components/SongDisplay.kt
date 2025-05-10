package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Column
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
import com.example.ratify.core.helper.SongDisplaySpecs
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
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
    val specs = SongDisplaySpecs

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
                    fontSize = specs.titleSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = specs.textPadding,
                            end = specs.textPadding
                        )
                )
                Text(
                    text = artists,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = specs.artistSize,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = specs.textPadding,
                            end = specs.textPadding,
                            bottom = specs.textPadding
                        )
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
@PreviewSuite
@Composable
fun SongDisplayPreviews() {
    MyPreview {
        SongDisplay(
            title = mockSong.name,
            artists = getArtistsString(mockSong.artists),
            imageUri = mockSong.imageUri.raw ?: ""
        )
    }
}

@PreviewSuite
@Composable
fun LongSongDisplayPreviews() {
    MyPreview {
        SongDisplay(
            title = longMockSong.name,
            artists = getArtistsString(longMockSong.artists),
            imageUri = longMockSong.imageUri.raw ?: ""
        )
    }
}
