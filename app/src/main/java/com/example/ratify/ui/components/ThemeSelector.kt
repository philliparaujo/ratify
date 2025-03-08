package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.settings.PrimaryColor
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun ThemeSelector(
    currentTheme: Int,
    onThemeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        val options = PrimaryColor.entries.toTypedArray()

        DropdownSelect(
            options = options.toList(),
            selectedOption = options[currentTheme],
            onSelect = { option -> onThemeSelected(option.ordinal)},
            label = "Theme",
            large = true
        )

//        Row(
//            horizontalArrangement = Arrangement.spacedBy(10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            val primaryColors = PrimaryColor.entries.toTypedArray()
//            primaryColors.forEach { primaryColor ->
//                Box(
//                    modifier = Modifier
//                        .size(32.dp)
//                        .clip(CircleShape)
//                        .background(primaryColor.color)
//                        .border(
//                            width = if (primaryColor.ordinal == currentTheme) 3.dp else 0.dp,
//                            color = MaterialTheme.colorScheme.primary,
//                            shape = CircleShape
//                        )
//                        .clickable {
//                            onThemeSelected(primaryColor.ordinal)
//                        }
//                )
//            }
//        }
//
//        val primaryColor = PrimaryColor.entries.toTypedArray()[currentTheme]
//        Text(text = "Theme: $primaryColor", fontWeight = FontWeight.Bold, color = primaryColor.color)
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