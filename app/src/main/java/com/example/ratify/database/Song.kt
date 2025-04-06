package com.example.ratify.database

import androidx.room.Entity
import com.example.ratify.core.model.Rating
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

// Individual entry in database
@Entity(tableName = "songs", primaryKeys = ["name", "artists"])
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
    val uri: String,

    // App info (may change)
    val lastPlayedTs: Long?,
    val timesPlayed: Int,
    val lastRatedTs: Long?,
    val rating: Rating?
)