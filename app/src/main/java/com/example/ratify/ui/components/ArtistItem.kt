package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.mocks.mockArtist
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ArtistItem(
    name: String,
    songCount: Int,
    averageRating: Float,
    imageUri: String,
    onClick: () -> Unit
) {
    val ratingText = averageRating.roundTo(1).toString()

    ImageOverlay(
        imageUri = imageUri,
        onClick = onClick,
        renderContent = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        ratingText,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        "$songCount entries",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 10.sp
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = name,
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
            }
        },
    )
}

fun Float.roundTo(n: Int): Float {
    val factor = 10.0.pow(n)
    return (this * factor).roundToInt() / factor.toFloat()
}

// Previews
@PreviewSuite
@Composable
fun ArtistItemPreviews() {
    MyPreview {
        Box(
            modifier = Modifier.fillMaxHeight().aspectRatio(1f),
        ) {
            ArtistItem(
                name = mockArtist.name,
                songCount = 2,
                averageRating = 6.4f,
                imageUri = "spotify:image:ab67616d0000b2739fe3277e1c1295755de75305",
                onClick = {}
            )
        }
    }
}