package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.core.model.PrimaryColor
import com.example.ratify.mocks.Preview

@Composable
fun ThemeSelector(
    currentTheme: Int,
    onThemeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = PrimaryColor.entries.toList()
    val selectedOption = options[currentTheme]

    ThemeDropdown(
        options = options,
        onThemeSelected = onThemeSelected,
        selectedOption = selectedOption,
        modifier = modifier
    )

}

@Composable
fun ThemeDropdown(
    options: List<PrimaryColor>,
    selectedOption: PrimaryColor,
    onThemeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val setExpanded = { newValue: Boolean -> expanded = newValue }

    val circleSize = 20.dp
    val selectionWidth = 160.dp

    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clickable color circle to trigger dropdown
            Box(
                modifier = Modifier
                    .size(circleSize)
                    .background(selectedOption.base, CircleShape)
                    .clickable { setExpanded(true) }
            )

            // Label text
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theme:",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedOption.toString(),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // The actual dropdown menu
        GenericDropdown(
            options = options,
            onSelect = { option -> onThemeSelected(option.ordinal) },
            renderText = {
                option -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .background(option.base, CircleShape)
                    )
                    Text(
                        text = option.toString(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            expanded = expanded,
            setExpanded = setExpanded,
            modifier = Modifier.width(selectionWidth)
        )
    }
}

// Previews
@Preview(name = "Dark Theme Selector")
@Composable
fun DarkThemeSelectorPreview() {
    Preview(darkTheme = true) {
        ThemeSelector(
            currentTheme = PrimaryColor.DEFAULT.ordinal,
            onThemeSelected = { }
        )
    }
}

@Preview(name = "Light Theme Selector")
@Composable
fun LightThemeSelectorPreview() {
    Preview(darkTheme = false) {
        ThemeSelector(
            currentTheme = PrimaryColor.DEFAULT.ordinal,
            onThemeSelected = { }
        )
    }
}