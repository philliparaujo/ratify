package com.example.ratify.spotifydatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

@Entity("songs")
data class Song(
    // Track info (unchanging)
    val album: Album,
    val artist: Artist,
    val artists: List<Artist>,
    val duration: Long,
    val imageUri: ImageUri,
//    val isEpisode: Boolean,
//    val isPodcast: Boolean,
    val name: String,

    @PrimaryKey(autoGenerate = false)
    val uri: String,

    // App info (may change)
    val lastPlayedTs: Long?,
    val lastRatedTs: Long?,
    val rating: Rating?
)
