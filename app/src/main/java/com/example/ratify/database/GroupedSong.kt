package com.example.ratify.database

import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

// Custom type of combined Song information by Artist or by Album
data class GroupedSong(
    val artist: Artist?,
    val album: Album?,
    val count: Int,
    val totalTimesPlayed: Int,
    val averageRating: Float,
    val lastPlayedTs: Long,
    val lastRatedTs: Long,
    val imageUri: ImageUri?
)
