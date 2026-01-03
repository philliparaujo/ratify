package com.example.ratify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.core.model.PlaylistCreationConfig
import com.example.ratify.core.model.SearchField
import com.example.ratify.core.model.defaultRatingMap
import com.example.ratify.core.model.getCurrentDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(
    onConfirm: (PlaylistCreationConfig) -> Unit,
    onDismiss: () -> Unit,
    onPreviewCount: suspend (PlaylistCreationConfig) -> Int
) {
    var playlistName by remember { mutableStateOf(getCurrentDate()) }
    var searchQuery by remember { mutableStateOf("") }
    var searchField by remember { mutableStateOf(SearchField.ARTIST) }
    var ratingMap by remember { mutableStateOf(defaultRatingMap()) }
    var expanded by remember { mutableStateOf(false) }
    var previewCount by remember { mutableStateOf<Int?>(null) }

    // Update preview count when config changes
    LaunchedEffect(searchQuery, searchField, ratingMap) {
        val config = PlaylistCreationConfig(
            searchQuery = searchQuery,
            searchField = searchField,
            ratingMap = ratingMap,
            playlistName = playlistName
        )
        previewCount = onPreviewCount(config)
    }

    GenericDialog(
        onDismissRequest = onDismiss,
        renderPortraitContent = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Create Playlist",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    // Playlist Name
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    HorizontalDivider()

                    // Search Field Selector
                    Text(
                        text = "Filter by:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = when (searchField) {
                                SearchField.ARTIST -> "Artist"
                                SearchField.ALBUM -> "Album"
                                SearchField.SONG_NAME -> "Song Name"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Search Field") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Artist") },
                                onClick = {
                                    searchField = SearchField.ARTIST
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Album") },
                                onClick = {
                                    searchField = SearchField.ALBUM
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Song Name") },
                                onClick = {
                                    searchField = SearchField.SONG_NAME
                                    expanded = false
                                }
                            )
                        }
                    }

                    // Search Query
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search") },
                        placeholder = { Text("Enter search term (or leave blank for all)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    HorizontalDivider()

                    // Rating Mapping
                    Text(
                        text = "Rating Mapping (# of entries):",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Two columns of rating mappings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left column (ratings 1-5)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (1..5).forEach { rating ->
                                RatingMapRow(
                                    rating = rating,
                                    value = ratingMap[rating] ?: 0,
                                    onValueChange = { newValue ->
                                        ratingMap = ratingMap.toMutableMap().apply {
                                            put(rating, newValue.coerceIn(0, 10))
                                        }
                                    }
                                )
                            }
                        }

                        // Right column (ratings 6-10)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            (6..10).forEach { rating ->
                                RatingMapRow(
                                    rating = rating,
                                    value = ratingMap[rating] ?: 0,
                                    onValueChange = { newValue ->
                                        ratingMap = ratingMap.toMutableMap().apply {
                                            put(rating, newValue.coerceIn(0, 10))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Preview
                    previewCount?.let { count ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(
                                text = if (count > 0) {
                                    "$count track${if (count != 1) "s" else ""} will be added to the playlist"
                                } else {
                                    "No tracks match the criteria"
                                },
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val config = PlaylistCreationConfig(
                                    searchQuery = searchQuery,
                                    searchField = searchField,
                                    ratingMap = ratingMap,
                                    playlistName = playlistName
                                )
                                onConfirm(config)
                            },
                            enabled = playlistName.isNotBlank() && (previewCount ?: 0) > 0
                        ) {
                            Text("Create Playlist")
                        }
                    }
                }

                // Close button in top right
                MyIconButton(
                    icon = ImageVector.vectorResource(R.drawable.baseline_close_24),
                    onClick = { onDismiss() },
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        renderLandscapeContent = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left column - Settings
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Create Playlist",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        OutlinedTextField(
                            value = playlistName,
                            onValueChange = { playlistName = it },
                            label = { Text("Playlist Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "Filter by:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = when (searchField) {
                                    SearchField.ARTIST -> "Artist"
                                    SearchField.ALBUM -> "Album"
                                    SearchField.SONG_NAME -> "Song Name"
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Search Field") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Artist") },
                                    onClick = {
                                        searchField = SearchField.ARTIST
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Album") },
                                    onClick = {
                                        searchField = SearchField.ALBUM
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Song Name") },
                                    onClick = {
                                        searchField = SearchField.SONG_NAME
                                        expanded = false
                                    }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search") },
                            placeholder = { Text("Enter search term") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        previewCount?.let { count ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text(
                                    text = if (count > 0) {
                                        "$count track${if (count != 1) "s" else ""} will be added"
                                    } else {
                                        "No tracks match"
                                    },
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    val config = PlaylistCreationConfig(
                                        searchQuery = searchQuery,
                                        searchField = searchField,
                                        ratingMap = ratingMap,
                                        playlistName = playlistName
                                    )
                                    onConfirm(config)
                                },
                                enabled = playlistName.isNotBlank() && (previewCount ?: 0) > 0
                            ) {
                                Text("Create")
                            }
                        }
                    }

                    // Right column - Rating Mapping
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Rating Mapping:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        (1..10).forEach { rating ->
                            RatingMapRow(
                                rating = rating,
                                value = ratingMap[rating] ?: 0,
                                onValueChange = { newValue ->
                                    ratingMap = ratingMap.toMutableMap().apply {
                                        put(rating, newValue.coerceIn(0, 10))
                                    }
                                }
                            )
                        }
                    }
                }

                // Close button in top right
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        }
    )
}

@Composable
private fun RatingMapRow(
    rating: Int,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rating:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(22.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Minus button
            IconButton(
                onClick = { if (value > 0) onValueChange(value - 1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrease",
                    modifier = Modifier.size(16.dp)
                )
            }

            // Value display
            OutlinedTextField(
                value = value.toString(),
                onValueChange = { newValue ->
                    val parsed = newValue.toIntOrNull()
                    if (parsed != null && parsed in 0..10) {
                        onValueChange(parsed)
                    } else if (newValue.isEmpty()) {
                        onValueChange(0)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(50.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Plus button
            IconButton(
                onClick = { if (value < 10) onValueChange(value + 1) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Increase",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
