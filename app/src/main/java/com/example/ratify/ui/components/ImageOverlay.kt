package com.example.ratify.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil3.compose.SubcomposeAsyncImage
import com.example.ratify.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageOverlay(
    imageUri: String,
    renderContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick ?: {},
                onLongClick = onLongClick
            )
    ) {
        // Image
        SubcomposeAsyncImage(
            model = imageUri,
            contentDescription = "Song image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            error = {
                // Checks if in Preview mode
                if (LocalInspectionMode.current) {
                    Image(
                        painter = painterResource(R.drawable.baseline_play_arrow_24),
                        contentDescription = "foo"
                    )
                }
            }
        )

        // Translucent black overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
        )

        // Other overlays
        renderContent()
    }
}