package com.example.ratify.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ratify.core.model.PlaylistCreationConfig
import com.example.ratify.core.model.SearchField
import com.example.ratify.core.model.defaultRatingMap
import com.example.ratify.core.model.getCurrentDate
import kotlinx.coroutines.launch

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

    val scope = rememberCoroutineScope()

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
                    singleLine = true
                )

                Divider()

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
                            .fillMaxWidth()
                            .menuAnchor()
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
                    singleLine = true
                )

                Divider()

                // Rating Mapping
                Text(
                    text = "Rating Mapping (Rating → # of entries):",
                    style = MaterialTheme.typography.titleMedium
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..10).forEach { rating ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rating $rating:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(100.dp)
                            )
                            OutlinedTextField(
                                value = (ratingMap[rating] ?: 0).toString(),
                                onValueChange = { newValue ->
                                    val count = newValue.toIntOrNull() ?: 0
                                    ratingMap = ratingMap.toMutableMap().apply {
                                        put(rating, count.coerceIn(0, 10))
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(80.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                Divider()

                // Preview
                previewCount?.let { count ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
        },
        renderLandscapeContent = {
            // For landscape, use a similar layout but potentially in a Row
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
                        singleLine = true
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
                                .fillMaxWidth()
                                .menuAnchor()
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
                        singleLine = true
                    )

                    previewCount?.let { count ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rating $rating:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.width(100.dp)
                            )
                            OutlinedTextField(
                                value = (ratingMap[rating] ?: 0).toString(),
                                onValueChange = { newValue ->
                                    val count = newValue.toIntOrNull() ?: 0
                                    ratingMap = ratingMap.toMutableMap().apply {
                                        put(rating, count.coerceIn(0, 10))
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(80.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }
        }
    )
}
