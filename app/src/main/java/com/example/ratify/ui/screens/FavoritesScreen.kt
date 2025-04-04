package com.example.ratify.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.FavoritesState
import com.example.ratify.spotifydatabase.GroupType
import com.example.ratify.spotifydatabase.SortType
import com.example.ratify.ui.components.AlbumItem
import com.example.ratify.ui.components.ArtistItem
import com.example.ratify.ui.components.DropdownSelect
import com.example.ratify.ui.components.MySlider
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.components.SongDisplay
import com.example.ratify.ui.components.spotifyUriToImageUrl
import com.example.ratify.ui.theme.RatifyTheme
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    val favoritesState = spotifyViewModel?.favoritesState?.collectAsState(initial = FavoritesState())?.value ?: FavoritesState()

    val sortTypes = listOf(SortType.RATING, SortType.LAST_PLAYED_TS, SortType.LAST_RATED_TS, SortType.TIMES_PLAYED, SortType.NAME)

    val maxValue: Long = 30
    var currentValue by remember { mutableLongStateOf(favoritesState.minEntriesThreshold.toLong()) }
    LaunchedEffect(favoritesState.minEntriesThreshold) {
        currentValue = favoritesState.minEntriesThreshold.toLong()
    }

    // Orientation logic
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    @Composable
    fun RenderSettings() {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                MySwitch(
                    leftText = "Artists",
                    rightText = "Albums",
                    checked = favoritesState.groupType == GroupType.ALBUM,
                    onCheckedChange = { newState ->
                        val newGroupType = if (newState) GroupType.ALBUM else GroupType.ARTIST
                        spotifyViewModel?.onEvent(SpotifyEvent.UpdateGroupType(newGroupType))
                    }
                )

                DropdownSelect(
                    options = sortTypes,
                    selectedOption = favoritesState.sortType,
                    onSelect = { sortType -> spotifyViewModel?.onEvent(SpotifyEvent.UpdateSortType(sortType)) },
                    label = "Sort by",
                    large = true
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MySlider(
                    currentValue = currentValue,
                    maxValue = maxValue,
                    onValueChanging = { newValue ->
                        currentValue = newValue.toFloat().roundToInt().toLong()
                    },
                    onValueChangeFinished = { newValue ->
                        val newInt = newValue.toFloat().roundToInt()
                        currentValue = newInt.toLong()
                        spotifyViewModel?.onEvent(SpotifyEvent.UpdateMinEntriesThreshold(newInt))
                    },
                    modifier = Modifier.weight(6f)
                )
                Text(
                    currentValue.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun RenderItemList() {
        val groupedSongs = favoritesState.groupedSongs
        val numRows = if (isLandscape) 4 else 2
        val rows = groupedSongs.chunked(numRows)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(rows) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { groupedSong ->
                        Box(modifier = Modifier.weight(1f)) {
                            when (favoritesState.groupType) {
                                GroupType.ARTIST -> {
                                    ArtistItem(
                                        name = groupedSong.artist?.name ?: "N/A",
                                        songCount = groupedSong.count,
                                        averageRating = groupedSong.averageRating,
                                        imageUri = spotifyUriToImageUrl(groupedSong.imageUri?.raw) ?: ""
                                    )
                                }
                                GroupType.ALBUM -> {
                                    AlbumItem(
                                        name = groupedSong.album?.name ?: "N/A",
                                        artistName = groupedSong.artist?.name ?: "N/A",
                                        songCount = groupedSong.count,
                                        averageRating = groupedSong.averageRating,
                                        imageUri = spotifyUriToImageUrl(groupedSong.imageUri?.raw) ?: ""
                                    )
                                }
                            }
                        }
                    }

                    // Ensure last row has same size
                    if (rowItems.size < numRows) {
                        repeat(numRows - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RenderSettings()

        HorizontalDivider()

        RenderItemList()
    }
}

// Previews
@Preview(name = "Dark Favorites Screen")
@Composable
fun DarkFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        FavoritesScreen(
            spotifyViewModel = null,
        )
    }
}

@Preview(name = "Light Favorites Screen")
@Composable
fun LightFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        FavoritesScreen(
            spotifyViewModel = null,
        )
    }
}

@Preview(
    name = "Dark Landscape Favorites Screen",
    device = landscapeDevice
)
@Composable
fun DarkLandscapeFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        FavoritesScreen(
            spotifyViewModel = null,
        )
    }
}

@Preview(
    name = "Light Landscape Favorites Screen",
    device = landscapeDevice
)
@Composable
fun LightLandscapeFavoritesScreenPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        FavoritesScreen(
            spotifyViewModel = null,
        )
    }
}