package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.mocks.MyPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "Search",
    trailingIcon: ImageVector?,
    dropdownLabels: List<String>,
    dropdownOptionOnClick: List<() -> Unit>
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {  _ -> },
                expanded = expanded,
                placeholder = { Text(placeholderText) },
                onExpandedChange = { _ -> },
                leadingIcon = { Icon(
                    Icons.Default.Search,
                    contentDescription = "Search icon"
                ) },
                trailingIcon = trailingIcon?.let {
                    {
                        Box {
                            IconButton(onClick = { isDropdownExpanded = true }) {
                                Icon(imageVector = it, contentDescription = "More search options")
                            }
                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                dropdownLabels.forEachIndexed { index, option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            dropdownOptionOnClick[index]()
                                            isDropdownExpanded = false
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
                },
                colors = SearchBarDefaults.inputFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        backgroundColor = MaterialTheme.colorScheme.secondary
                    ),
                    focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .padding(top = 0.dp)
            )
        }, expanded, { expanded = it }, modifier.padding(top = 0.dp),
        windowInsets = WindowInsets(
            left = 0, top = 0, right = 0, bottom = 0
        ),
        colors = SearchBarColors(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.background
        )
    ) { }
}

// Previews
@Preview(name = "Dark Search Bar")
@Composable
fun DarkSearchBarPreview() {
    MyPreview(darkTheme = true) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Search(
                    query = "Foo",
                    onQueryChange = { _ -> },
                    trailingIcon = null,
                    dropdownOptionOnClick = emptyList(),
                    dropdownLabels = emptyList()
                )
                Search(
                    query = "",
                    onQueryChange = { _ ->},
                    trailingIcon = Icons.Default.MoreVert,
                    dropdownLabels = listOf("Hide visualizer", "Delete unrated songs"),
                    dropdownOptionOnClick = listOf({}, {})
                )
            }
        }
    }
}

@Preview(name = "Light Search Bar")
@Composable
fun LightSearchBarPreview() {
    MyPreview(darkTheme = false) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Search(
                    query = "Foo",
                    onQueryChange = { _ -> },
                    trailingIcon = null,
                    dropdownOptionOnClick = emptyList(),
                    dropdownLabels = emptyList()
                )
                Search(
                    query = "",
                    onQueryChange = { _ ->},
                    trailingIcon = Icons.Default.MoreVert,
                    dropdownLabels = listOf("Hide visualizer", "Delete unrated songs"),
                    dropdownOptionOnClick = listOf({}, {})
                )
            }
        }
    }
}