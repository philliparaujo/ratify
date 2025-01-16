package com.example.ratify.ui.components

import android.graphics.PointF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.example.ratify.ui.theme.RatifyTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StarHalf(
    scale: Float = 1f,
    flipped: Boolean = false,
    initialStarColor: Color
) {
    var starColor by remember { mutableStateOf(initialStarColor) }
    val secondaryStarColor = MaterialTheme.colorScheme.secondary

    val radius = 0.4f * scale
    val innerRadius = radius / 2
    val rotationOffset = 90f

    // Generate the star
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

    Box(
        modifier = Modifier
            .size((70*scale).dp)
            .padding(2.dp)
            .clip(StarHalfShape(roundedPolygonPath))
            .background(starColor)
            .size((70*scale).dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { starColor = if (starColor == initialStarColor) secondaryStarColor else initialStarColor}
            )
    )
}

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

@Composable
fun Star(
    scale: Float = 1f,
    initialStarColor: Color
) {
    Box(
        modifier = Modifier
            .padding(0.dp)
    ) {
        StarHalf(scale = scale, initialStarColor = initialStarColor)
        StarHalf(scale = scale, flipped = true, initialStarColor = initialStarColor)
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
@Preview(name = "Star Preview")
@Composable
fun StarPreview() {
    RatifyTheme(darkTheme = true) {
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            repeat(5) {
                Star(scale = 1f, initialStarColor = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}