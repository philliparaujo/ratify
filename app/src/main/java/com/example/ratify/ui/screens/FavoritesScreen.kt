package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.spotifydatabase.FavoritesState
import com.example.ratify.ui.components.ArtistItem
import com.example.ratify.ui.components.MySlider
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.theme.RatifyTheme
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    val favoritesState = spotifyViewModel?.favoritesState?.collectAsState(initial = FavoritesState())?.value ?: FavoritesState()

    val maxValue: Long = 30
    var currentValue by remember { mutableLongStateOf(favoritesState.minEntriesThreshold.toLong()) }
    LaunchedEffect(favoritesState.minEntriesThreshold) {
        currentValue = favoritesState.minEntriesThreshold.toLong()
    }

    @Composable
    fun RenderSettings() {
        Column {
            MySwitch(
                leftText = "Artist view",
                rightText = "Album view",
                checked = false,
            )

            Row {
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
                    }
                )
            }

            Text(
                currentValue.toString(),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    @Composable
    fun RenderItemList() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(favoritesState.groupedSongs) { groupedSong ->
                ArtistItem(
                    name = groupedSong.artist?.name ?: "N/A",
                    songCount = groupedSong.count,
                    averageRating = groupedSong.averageRating,
                    imageUri = groupedSong.imageUri
                )
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