package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.core.helper.DropdownSpecs
import com.example.ratify.mocks.MyPreview

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelect(
    options: List<T>,
    selectedOption: T,
    onSelect: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    startExpanded: Boolean = false,
    large: Boolean = false,
) {
    var expanded by remember { mutableStateOf(startExpanded) }
    val setExpanded = { newValue: Boolean -> expanded = newValue }

    val specs = if (large) DropdownSpecs.LARGE else DropdownSpecs.SMALL

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { setExpanded(it) },
        modifier = Modifier
            .height(specs.height)
            .width(specs.width)
            .padding(0.dp)
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .height(specs.height)
                .width(specs.width)
        ) {
            TextField(
                value = selectedOption.toString(),
                onValueChange = {},
                readOnly = true,
                shape = CircleShape,
                label = { Text(text = label, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = specs.fontSize,
                    fontWeight = FontWeight.Bold,
                    baselineShift = BaselineShift(-1f)
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    disabledTextColor = MaterialTheme.colorScheme.onSecondary,
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.background,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                    disabledIndicatorColor = MaterialTheme.colorScheme.background,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    disabledLabelColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = modifier
                    .padding(0.dp)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
            )
        }
        GenericDropdown(
            options = options,
            onSelect = onSelect,
            renderText = { option ->
                Text(
                    text = option.toString(),
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                )
            },
            expanded = expanded,
            setExpanded = setExpanded,
            modifier = Modifier.width(specs.width)
        )
    }
}

// Previews
@Preview(name = "Dark Dropdown Select")
@Composable
fun DarkDropdownSelectPreview() {
    MyPreview(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            DropdownSelect(
                options = listOf(
                    "Name",
                    "Artists",
                    "Album",
                    "Rating"
                ),
                selectedOption = "Name",
                onSelect = {},
                label = "Search by"
            )

            DropdownSelect(
                options = listOf(
                    "Name",
                    "Artists",
                    "Album",
                    "Rating"
                ),
                selectedOption = "Artists",
                onSelect = {},
                label = "Search by",
                startExpanded = true
            )


            DropdownSelect(
                options = listOf(
                    "Rating",
                    "Last played",
                    "Last rated",
                    "Times played"
                ),
                selectedOption = "Last played",
                onSelect = {},
                label = "Sort by",
                startExpanded = true,
                large = true
            )
        }
    }
}

@Preview(name = "Light Dropdown Select")
@Composable
fun LightDropdownSelectPreview() {
    MyPreview(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            DropdownSelect(
                options = listOf(
                    "Name",
                    "Artists",
                    "Album",
                    "Rating"
                ),
                selectedOption = "Name",
                onSelect = {},
                label = "Search by"
            )

            DropdownSelect(
                options = listOf(
                    "Name",
                    "Artists",
                    "Album",
                    "Rating"
                ),
                selectedOption = "Artists",
                onSelect = {},
                label = "Search by",
                startExpanded = true
            )


            DropdownSelect(
                options = listOf(
                    "Rating",
                    "Last played",
                    "Last rated",
                    "Times played"
                ),
                selectedOption = "Last played",
                onSelect = {},
                label = "Sort by",
                startExpanded = true,
                large = true
            )
        }
    }
}