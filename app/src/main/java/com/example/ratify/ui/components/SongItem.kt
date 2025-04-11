package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.core.model.Rating
import com.example.ratify.database.Song
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.mockSong
import com.example.ratify.services.nullTextViewValue

@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPlay: () -> Unit,
    onDisabledPlay: () -> Unit = {},
    playEnabled: Boolean = true,
    showImageUri: Boolean,
) {
    GenericItem(
        title = song.name,
        subtitle = song.artists.joinToString(", ") { it.name },
        ratingColor = getRatingColor(song.rating),
        ratingText = (song.rating?.value ?: nullTextViewValue).toString(),
        displayButton = {
            MyIconButton(
                icon = ImageVector.vectorResource(id = R.drawable.baseline_play_arrow_24),
                onClick = onPlay,
                onDisabledClick = onDisabledPlay,
                enabled = playEnabled,
            )
        },
        onClick = onClick,
        onLongClick = onLongClick,
        imageUri = if (showImageUri) song.imageUri else null
    )
}

@Composable
fun getRatingColor(rating: Rating?): Color {
    val primaryColor = MaterialTheme.colorScheme.primary  // Rating = 10
    val lowRatingColor = MaterialTheme.colorScheme.inversePrimary  // Rating = 1
    val noRatingColor = MaterialTheme.colorScheme.background // No rating

    return when {
        rating == null -> noRatingColor
        rating.value <= 0 -> noRatingColor
        else -> {
            val normalizedRating = rating.value.coerceIn(1, 10) / 10f
            lerp(lowRatingColor, primaryColor, normalizedRating)
        }
    }
}


// Previews
@Preview(name = "Dark Song Items")
@Composable
fun DarkSongItemsPreview() {
    MyPreview(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Generate SongItems for ratings 10 to 1
            (10 downTo 1).forEach { ratingValue ->
                SongItem(
                    song = mockSong.copy(rating = Rating.from(ratingValue)),
                    onClick = { },
                    onLongClick = { },
                    onPlay = { },
                    onDisabledPlay = { },
                    showImageUri = false
                )
            }

            // Song with no rating
            SongItem(
                song = mockSong.copy(rating = null),
                onClick = { },
                onLongClick = { },
                onPlay = { },
                onDisabledPlay = { },
                showImageUri = false
            )
        }
    }
}

@Preview(name = "Light Song Items")
@Composable
fun LightSongItemsPreview() {
    MyPreview(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Generate SongItems for ratings 10 to 1
            (10 downTo 1).forEach { ratingValue ->
                SongItem(
                    song = mockSong.copy(rating = Rating.from(ratingValue)),
                    onClick = { },
                    onLongClick = { },
                    onPlay = { },
                    onDisabledPlay = { },
                    showImageUri = false
                )
            }

            // Song with no rating
            SongItem(
                song = mockSong.copy(rating = null),
                onClick = { },
                onLongClick = { },
                onPlay = { },
                onDisabledPlay = { },
                showImageUri = false
            )
        }
    }
}