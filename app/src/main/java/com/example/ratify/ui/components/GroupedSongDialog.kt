package com.example.ratify.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.core.model.GroupType
import com.example.ratify.database.GroupedSong
import com.example.ratify.database.Song
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.mocks.mockGroupedSong
import com.example.ratify.mocks.mockSong

@Composable
fun GroupedSongDialog(
    groupedSong: GroupedSong,
    groupType: GroupType,
    songs: List<Song>,
    onDismissRequest:() -> Unit,
    onLongClick: (Song) -> Unit,
    onPlay: (Song) -> Unit,
    onDisabledPlay: () -> Unit,
    showImageUri: Boolean = false,
    playEnabled: Boolean = false,
) {
    @Composable
    fun renderList(songs: List<Song>) {
        Log.d("GroupedSongDialog", songs.size.toString())

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (groupType) {
                        GroupType.ARTIST -> groupedSong.artist!!.name
                        GroupType.ALBUM -> groupedSong.album!!.name
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                MyIconButton(
                    icon = ImageVector.vectorResource(R.drawable.baseline_close_24),
                    onClick = { onDismissRequest() },
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        onClick = {},
                        onLongClick = { onLongClick(song) },
                        onPlay = { onPlay(song) },
                        onDisabledPlay = { onDisabledPlay() },
                        playEnabled = playEnabled,
                        showImageUri = showImageUri
                    )
                }
            }
        }
    }
    
    GenericDialog(
        renderLandscapeContent = { renderList(songs) },
        renderPortraitContent = { renderList(songs) },
        onDismissRequest = onDismissRequest
    )
}

// Previews
@PreviewSuite
@Composable
fun GroupedSongDialogPreviews() {
    MyPreview {
        GroupedSongDialog(
            groupedSong = mockGroupedSong,
            groupType = GroupType.ARTIST,
            songs = List(mockGroupedSong.count) { mockSong },
            onDismissRequest = { },
            onLongClick = { },
            onPlay = { },
            onDisabledPlay = { },
        )
    }
}