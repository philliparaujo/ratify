package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.settings.PrimaryColor
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun ThemeSelector(
    currentTheme: Int,
    onThemeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = PrimaryColor.entries.toTypedArray()
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
    options: Array<PrimaryColor>,
    selectedOption: PrimaryColor,
    onThemeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

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
                    .background(selectedOption.color, CircleShape)
                    .clickable { expanded = true }
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
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(selectionWidth),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(circleSize)
                                    .background(option.color, CircleShape)
                            )
                            Text(
                                text = option.toString(),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    onClick = {
                        onThemeSelected(option.ordinal)
                        expanded = false
                    },
                    colors = MenuItemColors(
                        textColor = MaterialTheme.colorScheme.onBackground,
                        leadingIconColor = MaterialTheme.colorScheme.onBackground,
                        trailingIconColor = MaterialTheme.colorScheme.onBackground,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

// Previews
@Preview(name = "Dark Theme Selector")
@Composable
fun DarkThemeSelectorPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        ThemeSelector(
            currentTheme = PrimaryColor.DEFAULT.ordinal,
            onThemeSelected = { }
        )
    }
}