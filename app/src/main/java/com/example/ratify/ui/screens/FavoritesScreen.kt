package com.example.ratify.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.ratify.spotifydatabase.FavoritesSortType
import com.example.ratify.spotifydatabase.FavoritesState
import com.example.ratify.spotifydatabase.GroupType
import com.example.ratify.spotifydatabase.GroupedSong
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.ui.components.AlbumItem
import com.example.ratify.ui.components.ArtistItem
import com.example.ratify.ui.components.DropdownSelect
import com.example.ratify.ui.components.GroupedSongDialog
import com.example.ratify.ui.components.MySlider
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.components.spotifyUriToImageUrl
import com.example.ratify.ui.theme.RatifyTheme
import kotlinx.coroutines.flow.flowOf
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen(
    spotifyViewModel: SpotifyViewModel?
) {
    val favoritesState = spotifyViewModel?.favoritesState?.collectAsState(initial = FavoritesState())?.value ?: FavoritesState()

    // Player enabled logic
    val userCapabilities = spotifyViewModel?.userCapabilities?.observeAsState()?.value
    val playerEnabled = userCapabilities?.canPlayOnDemand ?: false

    // Fetching songs for dialog based on selected group and group type
    val songs by remember(favoritesState.groupType, favoritesState.favoritesDialog) {
        val groupType = favoritesState.groupType
        val dialog = favoritesState.favoritesDialog

        val (groupName, groupUri) = when (favoritesState.groupType) {
            GroupType.ALBUM -> dialog?.album?.name to dialog?.album?.uri
            GroupType.ARTIST -> dialog?.artist?.name to dialog?.artist?.uri
        }

        groupName?.let { spotifyViewModel?.getSongsByGroup(groupType, it, groupUri!!) } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    // Sort button options
    val favoritesSortTypes = listOf(
        FavoritesSortType.RATING,
        FavoritesSortType.NAME,
        FavoritesSortType.NUM_ENTRIES,
        FavoritesSortType.TIMES_PLAYED,
        FavoritesSortType.LAST_RATED_TS,
        FavoritesSortType.LAST_PLAYED_TS
    )

    // Slider values
    val maxValue: Long = 30
    var currentValue by remember { mutableLongStateOf(favoritesState.minEntriesThreshold.toLong()) }
    LaunchedEffect(favoritesState.minEntriesThreshold) {
        currentValue = favoritesState.minEntriesThreshold.toLong()
    }

    // Orientation logic
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Settings variables
    val settings = spotifyViewModel?.settings
    val showImageUri = settings?.libraryImageUri?.collectAsState(true)
    val queueSkip = settings?.queueSkip?.collectAsState(false)
    fun realPlay(song: Song) {
        if (queueSkip?.value == true) {
            spotifyViewModel.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
            Thread.sleep(1000)
            spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
        } else {
            spotifyViewModel?.onEvent(SpotifyEvent.PlaySong(song.uri, song.name))
        }
    }

    @Composable
    fun RenderSettings() {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
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
                    options = favoritesSortTypes,
                    selectedOption = favoritesState.favoritesSortType,
                    onSelect = { sortType -> spotifyViewModel?.onEvent(SpotifyEvent.UpdateFavoritesSortType(sortType)) },
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
                                        imageUri = spotifyUriToImageUrl(groupedSong.imageUri?.raw) ?: "",
                                        onClick = { spotifyViewModel?.onEvent(SpotifyEvent.UpdateFavoritesDialog(groupedSong)) }
                                    )
                                }
                                GroupType.ALBUM -> {
                                    AlbumItem(
                                        name = groupedSong.album?.name ?: "N/A",
                                        artistName = groupedSong.artist?.name ?: "N/A",
                                        songCount = groupedSong.count,
                                        averageRating = groupedSong.averageRating,
                                        imageUri = spotifyUriToImageUrl(groupedSong.imageUri?.raw) ?: "",
                                        onClick = { spotifyViewModel?.onEvent(SpotifyEvent.UpdateFavoritesDialog(groupedSong)) }
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

    @Composable
    fun RenderDialog(groupedSong: GroupedSong, groupType: GroupType, songs: List<Song>) {
        AnimatedVisibility(
            visible = true,
        ) { }
        GroupedSongDialog(
            groupedSong = groupedSong,
            groupType = groupType,
            songs = songs,
            onDismissRequest = {
                spotifyViewModel?.onEvent(SpotifyEvent.UpdateFavoritesDialog(null))
            },
            playEnabled = playerEnabled,
            showImageUri = showImageUri?.value ?: false,
            onLongClick = { song ->
                if (playerEnabled) {
                    spotifyViewModel?.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
                } else {
                    spotifyViewModel?.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
                }
            },
            onPlay = { song -> realPlay(song) },
            onDisabledPlay = { spotifyViewModel?.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)}
        )
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
        if (favoritesState.favoritesDialog != null) {
            RenderDialog(
                favoritesState.favoritesDialog,
                favoritesState.groupType,
                songs
            )
        }
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