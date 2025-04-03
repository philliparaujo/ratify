package com.example.ratify.spotifydatabase

import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

data class GroupedSong(
    val artist: Artist?,
    val album: Album?,
    val count: Int,
    val averageRating: Float,
    val imageUri: ImageUri?,
)
