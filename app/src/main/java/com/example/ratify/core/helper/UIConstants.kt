package com.example.ratify.core.helper

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Sizes
enum class ButtonSpecs(
    val width: Dp,
    val height: Dp
) {
    LARGE(140.dp, 54.dp),
    SMALL(100.dp, 40.dp)
}

@Composable
fun ButtonSpecs.textStyle(): TextStyle {
    return when (this) {
        ButtonSpecs.LARGE -> MaterialTheme.typography.bodyLarge
        ButtonSpecs.SMALL -> MaterialTheme.typography.bodyMedium
    }
}


enum class DropdownSpecs(
    val width: Dp,
    val height: Dp,
    val fontSize: TextUnit
) {
    LARGE(150.dp, 56.dp, TextUnit(16f, TextUnitType.Sp)),
    SMALL(100.dp, 56.dp, TextUnit(16f, TextUnitType.Sp))
}


object ItemSpecs {
    val itemHeight = 55.dp
    val innerContentPadding = 3.dp
    val roundedCorner = RoundedCornerShape(12.dp)

    val ratingToContentSpacing = 10.dp
    val contentToButtonSpacing = 12.dp

    val ratingTextSize = 22.sp
    val titleSize = 16.sp
    val subtitleSize = 12.sp
}


enum class DialogSpecs(
    val outerHorizontalPadding: Dp,
    val outerVerticalPadding: Dp,
    val innerPadding: Dp,
    val innerScopeSpacing: Dp
) {
    LANDSCAPE(96.dp, 32.dp, 16.dp, 32.dp),
    PORTRAIT(32.dp, 160.dp, 16.dp, 16.dp)
}


enum class IconButtonSpecs(
    val buttonSize: Dp,
    val iconSize: Dp,
) {
    LARGE(80.dp, 50.dp),
    SMALL(50.dp, 30.dp)
}


object LeftNavBarSpecs {
    val width = 100.dp
}


object SliderSpecs {
    val circleSize = 12  // (dp)
    val barHeight = 8
}


object SnackBarSpecs {
    val height = 36.dp

    val outerHorizontalPadding = 8.dp
    val outerVerticalPadding = 6.dp
    val roundedCorner = 12.dp
    val innerHorizontalPadding = 12.dp
    val innerVerticalPadding = 8.dp

    val messageSize = 14.sp
}


object SongDisplaySpecs {
    val titleSize = 16.sp
    val artistSize = 14.sp

    val textPadding = 8.dp
}


object SwitchSpecs {
    val spacing = 16.dp
}


object ThemeSelectorSpecs {
    val circleSize = 20.dp
    val selectionWidth = 160.dp  // Not sure exactly what for, probably total dropdown width
    val colorTextSpacing = 12.dp
}


object VisualizerSpecs {
    val height = 50.dp
    val borderWeight = 0.5.dp
    val rounderCorner = 20.dp

    val outerPadding = 0.dp
    val innerHorizontalPadding = 16.dp
    val innerVerticalPadding = 8.dp

    val horizontalBarSpacing = 15.dp
    val verticalBarSpacing = 8.dp
}