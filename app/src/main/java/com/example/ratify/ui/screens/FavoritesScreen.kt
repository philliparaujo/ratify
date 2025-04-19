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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.core.helper.FAVORITES_MAX_SLIDER_VALUE
import com.example.ratify.core.helper.FAVORITES_SEARCH_TYPES
import com.example.ratify.core.helper.FAVORITES_SORT_TYPES
import com.example.ratify.core.helper.GroupsPerRow
import com.example.ratify.core.helper.LIBRARY_SEARCH_TYPES
import com.example.ratify.core.model.GroupType
import com.example.ratify.core.model.SearchType
import com.example.ratify.core.state.FavoritesState
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import com.example.ratify.di.LocalSettingsRepository
import com.example.ratify.di.LocalSongRepository
import com.example.ratify.di.LocalSpotifyViewModel
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.mocks.LANDSCAPE_DEVICE
import com.example.ratify.mocks.MyPreview
import com.example.ratify.repository.SettingsRepository
import com.example.ratify.repository.SongRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.spotify.ISpotifyViewModel
import com.example.ratify.spotify.SpotifyEvent
import com.example.ratify.ui.components.AlbumItem
import com.example.ratify.ui.components.ArtistItem
import com.example.ratify.ui.components.DropdownSelect
import com.example.ratify.ui.components.GroupedSongDialog
import com.example.ratify.ui.components.MySlider
import com.example.ratify.ui.components.MySwitch
import com.example.ratify.ui.components.Search
import com.example.ratify.ui.components.spotifyUriToImageUrl
import kotlinx.coroutines.flow.flowOf
import kotlin.math.roundToInt

@Composable
fun FavoritesScreen() {
    val spotifyViewModel: ISpotifyViewModel = LocalSpotifyViewModel.current
    val songRepository: SongRepository = LocalSongRepository.current
    val stateRepository: StateRepository = LocalStateRepository.current
    val settingsRepository: SettingsRepository = LocalSettingsRepository.current

    val favoritesState = stateRepository.favoritesState.collectAsState(initial = FavoritesState()).value

    // Player enabled logic
    val userCapabilities = spotifyViewModel.userCapabilities.observeAsState().value
    val playerEnabled = userCapabilities?.canPlayOnDemand ?: false

    // Fetching songs for dialog based on selected group and group type
    val songs by remember(favoritesState.groupType, favoritesState.dialog) {
        val groupType = favoritesState.groupType
        val dialog = favoritesState.dialog

        val (groupName, groupUri) = when (favoritesState.groupType) {
            GroupType.ALBUM -> dialog?.album?.name to dialog?.album?.uri
            GroupType.ARTIST -> dialog?.artist?.name to dialog?.artist?.uri
        }

        groupName?.let { songRepository.getSongsByGroup(groupType, it, groupUri!!) } ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    // Slider values
    var currentSliderValue by remember { mutableLongStateOf(favoritesState.minEntriesThreshold.toLong()) }
    LaunchedEffect(favoritesState.minEntriesThreshold) {
        currentSliderValue = favoritesState.minEntriesThreshold.toLong()
    }

    // Orientation logic
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Settings variables
    val showImageUri = settingsRepository.libraryImageUri.collectAsState(true)
    val queueSkip = settingsRepository.queueSkip.collectAsState(false)
    fun realPlay(song: Song) {
        if (queueSkip.value) {
            spotifyViewModel.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
            Thread.sleep(1000)
            spotifyViewModel.onEvent(SpotifyEvent.SkipNext)
        } else {
            spotifyViewModel.onEvent(SpotifyEvent.PlaySong(song.uri, song.name))
        }
    }

    // Handles up-to-date search query
    var localTextFieldValue by remember { mutableStateOf(TextFieldValue(favoritesState.searchQuery)) }

    @Composable
    fun RenderSearch(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Search(
                query = localTextFieldValue.text,
                onQueryChange = {
                    localTextFieldValue = localTextFieldValue.copy(text = it, selection = TextRange(it.length))
                    stateRepository.updateFavoritesSearchQuery(it)
                },
                placeholderText = "Search",
                trailingIcon = Icons.Default.MoreVert,
                dropdownLabels = emptyList(),
                dropdownOptionOnClick = emptyList(),
                modifier = Modifier.weight(1f)
            )

            // Searching dropdown
            DropdownSelect(
                options = FAVORITES_SEARCH_TYPES,
                selectedOption = favoritesState.searchType,
                onSelect = { searchType -> stateRepository.updateFavoritesSearchType(searchType) },
                label = "Search by",
                modifier = Modifier.wrapContentWidth()
            )
        }
    }

    @Composable
    fun RenderSettings(modifier: Modifier = Modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            MySwitch(
                leftText = "Artists",
                rightText = "Albums",
                checked = favoritesState.groupType == GroupType.ALBUM,
                onCheckedChange = { newState ->
                    val newGroupType = if (newState) GroupType.ALBUM else GroupType.ARTIST
                    stateRepository.updateGroupType(newGroupType)
                }
            )

            DropdownSelect(
                options = FAVORITES_SORT_TYPES,
                selectedOption = favoritesState.sortType,
                onSelect = { stateRepository.updateFavoritesSortType(it) },
                label = "Sort by",
                large = true
            )
        }
    }

    @Composable
    fun RenderSlider(modifier: Modifier = Modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier.width(96.dp).padding(end = 16.dp)
            ) {
                Text(
                    "≥$currentSliderValue entries",
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${favoritesState.groupedSongs.size} groups",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                )
            }

            MySlider(
                currentValue = currentSliderValue,
                maxValue = FAVORITES_MAX_SLIDER_VALUE,
                onValueChanging = { newValue ->
                    currentSliderValue = newValue.toFloat().roundToInt().toLong()
                },
                onValueChangeFinished = { newValue ->
                    val newInt = newValue.toFloat().roundToInt()
                    currentSliderValue = newInt.toLong()
                    stateRepository.updateMinEntriesThreshold(newInt)
                },
            )

        }
    }

    @Composable
    fun RenderItemList() {
        val groupedSongs = favoritesState.groupedSongs
        val groupsPerRow =
            if (isLandscape) GroupsPerRow.LANDSCAPE.columns else GroupsPerRow.PORTRAIT.columns
        val rows = groupedSongs.chunked(groupsPerRow)

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
                                        onClick = { stateRepository.updateFavoritesDialog(groupedSong) }
                                    )
                                }
                                GroupType.ALBUM -> {
                                    AlbumItem(
                                        name = groupedSong.album?.name ?: "N/A",
                                        artistName = groupedSong.artist?.name ?: "N/A",
                                        songCount = groupedSong.count,
                                        averageRating = groupedSong.averageRating,
                                        imageUri = spotifyUriToImageUrl(groupedSong.imageUri?.raw) ?: "",
                                        onClick = { stateRepository.updateFavoritesDialog(groupedSong) }
                                    )
                                }
                            }
                        }
                    }

                    // Ensure last row has same size
                    if (rowItems.size < groupsPerRow) {
                        repeat(groupsPerRow - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun RenderDialog(groupedSong: GroupedSong, groupType: GroupType, songs: List<Song>) {
        GroupedSongDialog(
            groupedSong = groupedSong,
            groupType = groupType,
            songs = songs,
            onDismissRequest = {
                stateRepository.updateFavoritesDialog(null)
            },
            playEnabled = playerEnabled,
            showImageUri = showImageUri.value,
            onLongClick = { song ->
                if (playerEnabled) {
                    spotifyViewModel.onEvent(SpotifyEvent.QueueTrack(song.uri, song.name))
                } else {
                    spotifyViewModel.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)
                }
            },
            onPlay = { song -> realPlay(song) },
            onDisabledPlay = { spotifyViewModel.onEvent(SpotifyEvent.PlayerEventWhenNotConnected)}
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLandscape) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                RenderSearch(modifier = Modifier.weight(4f))
                RenderSlider(modifier = Modifier.weight(3f))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RenderSettings(modifier = Modifier.weight(4f))
                Spacer(modifier = Modifier.weight(3f))
            }
        } else {
            RenderSearch(modifier = Modifier.fillMaxWidth())
            RenderSlider()
            RenderSettings(modifier = Modifier.fillMaxWidth())
        }

        HorizontalDivider()

        RenderItemList()
        if (favoritesState.dialog != null) {
            RenderDialog(
                favoritesState.dialog,
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
    MyPreview(darkTheme = true) {
        FavoritesScreen()
    }
}

@Preview(name = "Light Favorites Screen")
@Composable
fun LightFavoritesScreenPreview() {
    MyPreview(darkTheme = false) {
        FavoritesScreen()
    }
}

@Preview(
    name = "Dark Landscape Favorites Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun DarkLandscapeFavoritesScreenPreview() {
    MyPreview(darkTheme = true) {
        FavoritesScreen()
    }
}

@Preview(
    name = "Light Landscape Favorites Screen",
    device = LANDSCAPE_DEVICE
)
@Composable
fun LightLandscapeFavoritesScreenPreview() {
    MyPreview(darkTheme = false) {
        FavoritesScreen()
    }
}