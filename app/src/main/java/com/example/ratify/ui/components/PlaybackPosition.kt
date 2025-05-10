package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite

@Composable
fun PlaybackPosition(
    currentPositionMs: Long,
    totalDurationMs: Long,
    onValueChanging: ((Long) -> Unit)? = null,
    onValueChangeFinished: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        MySlider(
            currentValue = currentPositionMs,
            maxValue = totalDurationMs,
            onValueChanging = onValueChanging,
            onValueChangeFinished = onValueChangeFinished,
            enabled = enabled,
        )

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

// Previews
@PreviewSuite
@Composable
fun PlaybackPositionPreviews() {
    MyPreview {
        Column {
            PlaybackPosition(
                currentPositionMs = 105000L, // 1:45
                totalDurationMs = 300000L,  // 5:00
                modifier = Modifier.padding(16.dp)
            )

            PlaybackPosition(
                currentPositionMs = 0L, // 0:00
                totalDurationMs = 300000L,  // 5:00
                modifier = Modifier.padding(16.dp)
            )

            PlaybackPosition(
                currentPositionMs = 300000L, // 5:00
                totalDurationMs = 300000L,  // 5:00
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}