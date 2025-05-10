package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.database.Song
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.mocks.mockSong
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SongDialog(
    song: Song,
    onRatingSelect: (Int) -> Unit,
    onPlay: () -> Unit,
    onDisabledPlay: () -> Unit = {},
    onDelete: () -> Unit,
    onDismissRequest: () -> Unit,
    playEnabled: Boolean = true,
    deleteEnabled: Boolean = true,
) {
    val textColor = MaterialTheme.colorScheme.onBackground

    @Composable
    fun RenderSongDisplay() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SongDisplay(
                title = song.name,
                artists = getArtistsString(song.artists),
                imageUri = spotifyUriToImageUrl(song.imageUri.raw) ?: ""
            )
        }
    }

    @Composable
    fun RenderSongControls() {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            MyButton(
                text = "Play",
                enabled = playEnabled,
                onClick = {
                    onPlay()
                    onDismissRequest()
                },
                onDisabledClick = {
                    onDisabledPlay()
                }
            )
            MyButton(
                text = "Delete",
                enabled = deleteEnabled,
                onClick = {
                    onDelete()
                    onDismissRequest()
                }
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Duration: ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(formatTime(song.duration))
                        }
                    },
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Last Rated: ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(timestampToDate(song.lastRatedTs) ?: "N/A")
                        }
                    },
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Times Played: ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(song.timesPlayed.toString())
                        }
                    },
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Last Played: ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(timestampToDate(song.lastPlayedTs) ?: "N/A")
                        }
                    },
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        StarRow(
            scale = 0.8f,
            starCount = 5,
            onRatingSelect = { rating -> onRatingSelect(rating) },
            currentRating = song.rating
        )
    }

    GenericDialog(
        renderLandscapeContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.aspectRatio(1f)
                ) {
                    RenderSongDisplay()
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(
                            space = 24.dp,
                            alignment = Alignment.Bottom
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RenderSongControls()
                    }
                    MyIconButton(
                        icon = ImageVector.vectorResource(R.drawable.baseline_close_24),
                        onClick = { onDismissRequest() },
                    )
                }
            }
        },
        renderPortraitContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                RenderSongDisplay()
                MyIconButton(
                    icon = ImageVector.vectorResource(R.drawable.baseline_close_24),
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp)
                )
            }
            RenderSongControls()
        },
        onDismissRequest = onDismissRequest
    )
}

fun timestampToDate(timestamp: Long?): String? {
    if (timestamp == null) {
        return null
    }

    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(date)
}

// Previews
@PreviewSuite
@Composable
fun SongDialogPreviews() {
    MyPreview {
        SongDialog(
            song = mockSong,
            onDismissRequest = {},
            onRatingSelect = { _ -> },
            onPlay = {},
            onDisabledPlay = {},
            onDelete = {}
        )
    }
}