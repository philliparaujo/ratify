package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.Text
import com.example.ratify.spotifydatabase.Rating
import com.example.ratify.ui.theme.RatifyTheme
import kotlin.math.max

@Composable
fun Visualizer(
    heights: List<Float>
) {
    // Normalization value
    val maxHeight = max(heights.max(), 1f)

    val totalHeight = 50.dp
    val horizontalInnerPadding = 16.dp
    val verticalInnerPadding = 8.dp
    val outerPadding = 0.dp
    val spacing = 15.dp
    val columnSpacing = 8.dp
    val borderWeight = 0.5.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = outerPadding)
            .border(width = borderWeight, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(horizontal = horizontalInnerPadding, vertical = verticalInnerPadding)
        ) {
            heights.forEachIndexed({ i,h ->
                val ratingValue = i+1

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(columnSpacing)
                ) {
                    Box(modifier = Modifier
                        .height(h/maxHeight * totalHeight)
                        .fillMaxWidth()
                        .background(getRatingColor(Rating.from(ratingValue))
                    ))
                    Text(text = "${ratingValue}", color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            })
        }
    }
}

// Previews
@Preview(name = "Visualizer")
@Composable
fun VisualizerPreview() {
    RatifyTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Visualizer(
                heights = listOf(50f, 150f, 30f, 1f, 50f, 50f, 50f, 50f, 50f, 50f)
            )
        }
    }
}