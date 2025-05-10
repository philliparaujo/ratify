package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import androidx.wear.compose.material.Text
import com.example.ratify.core.helper.VisualizerSpecs
import com.example.ratify.core.model.Rating
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import kotlin.math.max

@Composable
fun Visualizer(
    heights: List<Float>
) {
    // Normalization value
    val maxHeight = max(heights.max(), 1f)

    val specs = VisualizerSpecs

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = specs.outerPadding)
            .border(width = specs.borderWeight, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(specs.rounderCorner))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(specs.horizontalBarSpacing),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(horizontal = specs.innerHorizontalPadding, vertical = specs.innerVerticalPadding)
        ) {
            heights.forEachIndexed { i, h ->
                val ratingValue = i + 1

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(specs.verticalBarSpacing)
                ) {
                    Box(
                        modifier = Modifier
                            .height(h / maxHeight * specs.height)
                            .fillMaxWidth()
                            .background(
                                getRatingColor(Rating.from(ratingValue))
                            )
                    )
                    Text(
                        text = "$ratingValue",
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// Previews
@PreviewSuite
@Composable
fun VisualizerPreviews() {
    MyPreview {
        Visualizer(
            heights = listOf(5f, 10f, 20f, 30f, 40f, 60f, 40f, 20f, 30f, 90f)
        )
    }
}