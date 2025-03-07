package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.ui.screens.landscapeDevice
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun BinarySetting(
    displayText: String,
    state: Boolean,
    toggleState: ((Boolean) -> Unit)?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Checkbox(
            checked = state,
            onCheckedChange = { newState ->
                toggleState?.invoke(newState)
            },
            colors = CheckboxColors(
                checkedCheckmarkColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedCheckmarkColor = MaterialTheme.colorScheme.secondary,
                checkedBoxColor = MaterialTheme.colorScheme.primary,
                uncheckedBoxColor = MaterialTheme.colorScheme.secondary,
                checkedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedBorderColor = MaterialTheme.colorScheme.secondary,
                disabledCheckedBoxColor = Color.Unspecified,
                disabledUncheckedBoxColor = Color.Unspecified,
                disabledIndeterminateBoxColor = Color.Unspecified,
                disabledBorderColor = Color.Unspecified,
                disabledUncheckedBorderColor = Color.Unspecified,
                disabledIndeterminateBorderColor = Color.Unspecified,
            )
        )

        Text(
            text = displayText,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// Previews
@Preview(name = "Dark BinarySettings")
@Composable
fun DarkBinarySettingsPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BinarySetting(
                displayText = "Default setting",
                state = true,
                toggleState = { }
            )
            BinarySetting(
                displayText = "Other Default setting",
                state = false,
                toggleState = { }
            )
        }
    }
}

@Preview(name = "Light BinarySettings")
@Composable
fun LightBinarySettingsPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BinarySetting(
                displayText = "Default setting",
                state = true,
                toggleState = { }
            )
            BinarySetting(
                displayText = "Other Default setting",
                state = false,
                toggleState = { }
            )
        }
    }
}

@Preview(
    name = "Dark Landscape BinarySettings",
    device = landscapeDevice
)
@Composable
fun DarkLandscapeBinarySettingsPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BinarySetting(
                displayText = "Default setting",
                state = true,
                toggleState = { }
            )
            BinarySetting(
                displayText = "Other Default setting",
                state = false,
                toggleState = { }
            )
        }
    }
}

@Preview(
    name = "Light Landscape BinarySettings",
    device = landscapeDevice
)
@Composable
fun LightLandscapeBinarySettings() {
    RatifyTheme(
        darkTheme = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BinarySetting(
                displayText = "Default setting",
                state = true,
                toggleState = { }
            )
            BinarySetting(
                displayText = "Other Default setting",
                state = false,
                toggleState = { }
            )
        }
    }
}