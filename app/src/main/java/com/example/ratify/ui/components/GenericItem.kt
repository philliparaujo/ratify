package com.example.ratify.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.spotify.protocol.types.ImageUri

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GenericItem(
    title: String,
    subtitle: String,
    ratingColor: Color,
    ratingText: String,
    displayButton: (@Composable () -> Unit)?,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    imageUri: ImageUri?
) {
    val itemHeight = 55.dp
    val innerContentPadding = 3.dp
    val roundedCorner = RoundedCornerShape(12.dp)

    val ratingToContentSpacing = 10.dp
    val contentToButtonSpacing = 12.dp

    val ratingTextSize = 22.sp
    val titleSize = 16.sp
    val subtitleSize = 12.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clip(roundedCorner)
            .border(0.5.dp, MaterialTheme.colorScheme.secondary, roundedCorner)
            .background(MaterialTheme.colorScheme.background)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        if (imageUri != null) {
            // Background Image
            SubcomposeAsyncImage(
                model = spotifyUriToImageUrl(imageUri.raw),
                contentDescription = "Song Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(roundedCorner)
            )

            // Translucent overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
            )
        }

        // Foreground Content (Row)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating Box
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxHeight()
                    .clip(roundedCorner)
                    .background(ratingColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ratingText,
                    fontSize = ratingTextSize,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(ratingToContentSpacing))

            // Title and subtitle
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = subtitleSize,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(contentToButtonSpacing))

            if (displayButton != null) {
                displayButton()
            }
        }
    }
}