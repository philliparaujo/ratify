package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.ratify.core.helper.SliderSpecs
import com.example.ratify.mocks.MyPreview

@Composable
fun MySlider(
    currentValue: Long,
    maxValue: Long,
    onValueChanging: ((Long) -> Unit)? = null,
    onValueChangeFinished: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var containerWidthPx by remember { mutableIntStateOf(0) }

    var draggedValue by remember { mutableLongStateOf(currentValue) }
    var realMaxValue by remember { mutableLongStateOf(maxValue) }
    LaunchedEffect(currentValue, maxValue) {
        draggedValue = currentValue
        realMaxValue = maxValue
    }
    val progress = draggedValue.toFloat() / realMaxValue.toFloat().coerceAtLeast(1f)

    val specs = SliderSpecs

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val tappedProgress = offset.x / containerWidthPx
                    val newValue = (tappedProgress * realMaxValue).toLong().coerceIn(0, realMaxValue)
                    onValueChanging?.invoke(newValue)
                    onValueChangeFinished?.invoke(newValue)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onValueChangeFinished?.invoke(draggedValue)
                    },
                    onDrag = { change, _ ->
                        val draggedProgress = change.position.x / containerWidthPx
                        val newValue = (draggedProgress * realMaxValue).toLong().coerceIn(0, realMaxValue)
                        draggedValue = newValue
                        onValueChanging?.invoke(newValue)
                    }
                )
            }
            .fillMaxWidth()
            .height(specs.circleSize.dp)
            .onSizeChanged { containerWidthPx = it.width }
    ) {
        // Secondary progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(specs.barHeight.dp)
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
                .height(specs.barHeight.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
                .align(Alignment.CenterStart)
        )

        // Progress circle
        Box(
            modifier = Modifier
                .size(specs.circleSize.dp)
                .offset {
                    val offsetX = (progress * (containerWidthPx.toFloat() - specs.circleSize) - specs.circleSize).toInt() // Center the circle
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
}

// Previews
@Preview(name = "Dark Slider")
@Composable
fun DarkSliderPreview() {
    MyPreview(darkTheme = true) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MySlider(
                20,
                100,
            )
        }
    }
}

@Preview(name = "Light Slider")
@Composable
fun LightSliderPreview() {
    MyPreview(darkTheme = false) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MySlider(
                95,
                100,
            )
        }
    }
}