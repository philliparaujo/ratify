package com.example.ratify.ui.components

import android.graphics.PointF
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.example.ratify.core.model.Rating
import com.example.ratify.mocks.MyPreview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val BASE_STAR_SIZE = 70f

@Composable
fun StarHalf(
    scale: Float = 1f,
    flipped: Boolean = false,
    selectedStarColor: Color,
    deselectedStarColor: Color,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    // Class that creates a shape object to clip with
    class StarHalfShape(
        private val path: Path
    ) : Shape {
        private val matrix = Matrix()

        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            matrix.scale(size.width, size.height)
            matrix.translate(0.5f, 0.5f)

            path.transform(matrix)
            return Outline.Generic(path)
        }
    }

    // Sizing and rotation parameters
    val normalizedScale = scale.coerceAtLeast(0.0f).coerceAtMost(1.0f)
    val adjustedScale = sqrt(normalizedScale.toDouble()).toFloat()

    val radius = 0.4f * adjustedScale
    val innerRadius = radius / 2
    val rotationOffset = 90f

    // Generate the star path
    val vertices = remember {
        val angles = listOf(0f, 36f, 72f, 108f, 144f, 180f).map { it + rotationOffset }
        angles.flatMapIndexed { index, angle ->
            val radiusToUse = if (index % 2 == 0) innerRadius else radius
            val point = radialToCartesian(radiusToUse, angle.toRadians())
            listOf(if (flipped) -point.x else point.x, point.y)
        }.toFloatArray()
    }
    val polygon = remember(vertices) {
        RoundedPolygon(vertices = vertices)
    }
    val roundedPolygonPath = polygon.toPath().asComposePath()

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val starColor = if (selected) selectedStarColor else deselectedStarColor
    Box(
        modifier = Modifier
            .size((BASE_STAR_SIZE * scale).dp)
            .clip(StarHalfShape(roundedPolygonPath))
            .background(starColor)
            .size((BASE_STAR_SIZE * scale).dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    )
}

@Composable
fun Star(
    scale: Float = 1f,
    selectedStarColor: Color,
    deselectedStarColor: Color,
    isLeftSelected: Boolean,
    isRightSelected: Boolean,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(0.dp)
    ) {
        // Left half
        StarHalf(
            scale = scale,
            flipped = false,
            selectedStarColor = selectedStarColor,
            deselectedStarColor = deselectedStarColor,
            onClick = onLeftClick,
            selected = isLeftSelected
        )

        // Right half
        StarHalf(
            scale = scale,
            flipped = true,
            selectedStarColor = selectedStarColor,
            deselectedStarColor = deselectedStarColor,
            onClick = onRightClick,
            selected = isRightSelected
        )
    }
}

@Composable
fun StarRow(
    scale: Float = 1f,
    selectedStarColor: Color = MaterialTheme.colorScheme.tertiary,
    deselectedStarColor: Color = MaterialTheme.colorScheme.secondary,
    starCount: Int = 5,
    onRatingSelect: (Int) -> Unit,
    currentRating: Rating? = null,
    enabled: Boolean = true,
) {
    var ratingValue by remember { mutableIntStateOf(currentRating?.value ?: 0) }
    val starStates = remember { mutableStateListOf(*Array(starCount * 2) { false }) }

    fun updateStarSelections(selectedIndex: Int) {
        if (!enabled) {
            return
        }

        ratingValue = selectedIndex + 1
        for (j in starStates.indices) {
            starStates[j] = j <= selectedIndex
        }
    }

    // Synchronize with external `currentRating`
    LaunchedEffect(currentRating) {
        val newRatingValue = currentRating?.value ?: 0
        ratingValue = newRatingValue
        updateStarSelections(newRatingValue - 1)
    }

    Row(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val positionX = change.position.x
                        val starWidth = BASE_STAR_SIZE.dp.toPx() * scale
                        val starIndex = (positionX / (starWidth / 2)).toInt()

                        if (starIndex in starStates.indices) {
                            updateStarSelections(starIndex)
                        }
                    },
                    onDragEnd = {
                        if (enabled) {
                            onRatingSelect(ratingValue)
                        }
                    }
                )
            }
    ) {
        for (i in 0 until starCount) {
            val leftStarIndex = i * 2
            val rightStarIndex = i * 2 + 1

            Star(
                scale = scale,
                selectedStarColor = selectedStarColor,
                deselectedStarColor = deselectedStarColor,
                isLeftSelected = starStates[leftStarIndex],
                isRightSelected = starStates[rightStarIndex],
                onLeftClick = {
                    updateStarSelections(leftStarIndex)
                    if (enabled) {
                        onRatingSelect(ratingValue)
                    }
                },
                onRightClick = {
                    updateStarSelections(rightStarIndex)
                    if (enabled) {
                        onRatingSelect(ratingValue)
                    }
                }
            )
        }
    }
}

// Helper functions for coordinate calculations
fun radialToCartesian(radius: Float, angleRadians: Float): PointF {
    return PointF(
        cos(angleRadians) * radius,
        sin(angleRadians) * radius
    )
}
fun Float.toRadians() = this * (PI.toFloat() / 180f)

// Previews
@Preview(name = "Dark Star Preview")
@Composable
fun DarkStarPreview() {
    MyPreview(darkTheme = true) {
        Column {
            Star(
                scale = 1f,
                selectedStarColor = MaterialTheme.colorScheme.tertiary,
                deselectedStarColor = MaterialTheme.colorScheme.secondary,
                isLeftSelected = true,
                isRightSelected = true,
                onLeftClick = {},
            ) {}

            Star(
                scale = 2f,
                selectedStarColor = MaterialTheme.colorScheme.tertiary,
                deselectedStarColor = MaterialTheme.colorScheme.secondary,
                isLeftSelected = true,
                isRightSelected = true,
                onLeftClick = {},
            ) {}


            Star(
                scale = 0.5f,
                selectedStarColor = MaterialTheme.colorScheme.tertiary,
                deselectedStarColor = MaterialTheme.colorScheme.secondary,
                isLeftSelected = true,
                isRightSelected = true,
                onLeftClick = {},
            ) {}
        }
    }
}

@Preview(name = "Light Star Preview")
@Composable
fun LightStarPreview() {
    MyPreview(darkTheme = false) {
        Column {
            Star(
                scale = 1f,
                selectedStarColor = MaterialTheme.colorScheme.tertiary,
                deselectedStarColor = MaterialTheme.colorScheme.secondary,
                isLeftSelected = true,
                isRightSelected = true,
                onLeftClick = {},
            ) {}

            Star(
                scale = 2f,
                selectedStarColor = MaterialTheme.colorScheme.tertiary,
                deselectedStarColor = MaterialTheme.colorScheme.secondary,
                isLeftSelected = true,
                isRightSelected = true,
                onLeftClick = {},
            ) {}


            Star(
                scale = 0.5f,
                selectedStarColor = MaterialTheme.colorScheme.tertiary,
                deselectedStarColor = MaterialTheme.colorScheme.secondary,
                isLeftSelected = true,
                isRightSelected = true,
                onLeftClick = {},
            ) {}
        }
    }
}

@Preview(name = "Dark Star Row Preview")
@Composable
fun DarkStarRowPreview() {
    var rating by remember { mutableIntStateOf(0) }

    MyPreview(darkTheme = true) {
        Column {
            StarRow(
                scale = 1f,
                starCount = 5,
                onRatingSelect = { newRating ->
                    Log.e("StarRow", "Selected value: $newRating")
                    rating = newRating
                },
                currentRating = if (rating > 0) Rating.from(rating) else null
            )
            StarRow(
                scale = 0.5f,
                starCount = 5,
                onRatingSelect = { newRating ->
                    Log.e("StarRow", "Selected value: $newRating")
                    rating = newRating
                },
                currentRating = if (rating > 0) Rating.from(rating) else null
            )
            StarRow(
                scale = 2f,
                starCount = 5,
                onRatingSelect = { newRating ->
                    Log.e("StarRow", "Selected value: $newRating")
                    rating = newRating
                },
                currentRating = if (rating > 0) Rating.from(rating) else null
            )
        }
    }
}

@Preview(name = "Light Star Row Preview")
@Composable
fun LightStarRowPreview() {
    var rating by remember { mutableIntStateOf(0) }

    MyPreview(darkTheme = false) {
        Column {
            StarRow(
                scale = 1f,
                starCount = 5,
                onRatingSelect = { newRating ->
                    Log.e("StarRow", "Selected value: $newRating")
                    rating = newRating
                },
                currentRating = if (rating > 0) Rating.from(rating) else null
            )
            StarRow(
                scale = 0.5f,
                starCount = 5,
                onRatingSelect = { newRating ->
                    Log.e("StarRow", "Selected value: $newRating")
                    rating = newRating
                },
                currentRating = if (rating > 0) Rating.from(rating) else null
            )
            StarRow(
                scale = 2f,
                starCount = 5,
                onRatingSelect = { newRating ->
                    Log.e("StarRow", "Selected value: $newRating")
                    rating = newRating
                },
                currentRating = if (rating > 0) Rating.from(rating) else null
            )
        }
    }
}