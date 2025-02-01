package com.example.ratify.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.R
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.ui.theme.MyDarkCyan
import com.example.ratify.ui.theme.RatifyTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    playEnabled: Boolean = true,
) {
    val itemHeight = 55.dp
    val innerContentPadding = 3.dp
    val roundedCorner = RoundedCornerShape(12.dp)

    val ratingToContentSpacing = 10.dp
    val contentToButtonSpacing = 12.dp

    val ratingTextSize = 22.sp
    val titleSize = 16.sp
    val artistsSize = 12.sp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clip(roundedCorner)
            .border(0.5.dp, MaterialTheme.colorScheme.secondary, roundedCorner)
            .background(MaterialTheme.colorScheme.background)
            .padding(innerContentPadding)
            .combinedClickable(
                onClick = onClick, // Ensure the entire row is clickable
                onLongClick = { /* Optional: Handle long-press if needed */ }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rating box
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxHeight()
                .clip(roundedCorner)
                .background(getRatingColor(song.rating)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = song.rating?.value?.toString() ?: "-",
                fontSize = ratingTextSize,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(ratingToContentSpacing))

        // Song name and artist
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.name,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artists.joinToString(", ") { it.name }, // Show all artists
                fontSize = artistsSize,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(contentToButtonSpacing))

        // Play button
        MyIconButton(
            icon = ImageVector.vectorResource(id = R.drawable.baseline_play_arrow_24),
            onClick = onPlay,
            enabled = playEnabled,
        )
    }
}

@Composable
public fun getRatingColor(rating: Rating?): Color {
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
@Preview(name = "Song Item")
@Composable
fun SongItemPreview() {
    RatifyTheme(darkTheme = true) {
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
                    onPlay = { }
                )
            }

            // Song with no rating
            SongItem(
                song = mockSong.copy(rating = null),
                onClick = { },
                onPlay = { }
            )
        }
    }
}