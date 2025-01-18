package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun PlaybackPosition(
    currentPositionMs: Long,
    totalDurationMs: Long,
    modifier: Modifier = Modifier
) {
    var containerWidthPx by remember { mutableIntStateOf(0) }
    val progress = currentPositionMs.toFloat() / totalDurationMs.toFloat()
    val progressCircleSize = 12

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .onSizeChanged { containerWidthPx = it.width }
        ) {
            // Secondary progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .align(Alignment.CenterStart)
            )

            // Primary progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .align(Alignment.CenterStart)
            )

            // Progress circle
            Box(
                modifier = Modifier
                    .size(progressCircleSize.dp)
                    .offset {
                        val offsetX = (progress * (containerWidthPx.toFloat() - progressCircleSize) - progressCircleSize).toInt() // Center the circle
                        IntOffset(x = offsetX, y = 0)
                    }
                    .background(
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterStart)
                    .zIndex(1f)
            )
        }

        // Time indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPositionMs),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = formatTime(totalDurationMs),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

// Helper function to format time in ms to mm:ss
fun formatTime(timeMs: Long): String {
    val minutes = (timeMs / 1000) / 60
    val seconds = (timeMs / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}

// Preview
@Preview(showBackground = true)
@Composable
fun PlayerPositionPreview() {
    RatifyTheme(darkTheme = true) {
        Column {
            Box(
                Modifier.background(color = MaterialTheme.colorScheme.background)
            ) {
                PlaybackPosition(
                    currentPositionMs = 105000L, // 1:45
                    totalDurationMs = 300000L,  // 5:00
                    modifier = Modifier.padding(16.dp)
                )
            }

            Box(
                Modifier.background(color = MaterialTheme.colorScheme.background)
            ) {
                PlaybackPosition(
                    currentPositionMs = 0L, // 0:00
                    totalDurationMs = 300000L,  // 5:00
                    modifier = Modifier.padding(16.dp)
                )
            }

            Box(
                Modifier.background(color = MaterialTheme.colorScheme.background)
            ) {
                PlaybackPosition(
                    currentPositionMs = 300000L, // 5:00
                    totalDurationMs = 300000L,  // 5:00
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
