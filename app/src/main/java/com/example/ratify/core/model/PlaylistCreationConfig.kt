package com.example.ratify.core.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PlaylistCreationConfig(
    val searchQuery: String = "",
    val searchField: SearchField = SearchField.ARTIST,
    val ratingMap: Map<Int, Int> = defaultRatingMap(),
    val playlistName: String = getCurrentDate()
)

enum class SearchField {
    ARTIST,
    ALBUM,
    SONG_NAME
}

fun defaultRatingMap(): Map<Int, Int> = mapOf(
    1 to 0,
    2 to 0,
    3 to 0,
    4 to 0,
    5 to 0,
    6 to 0,
    7 to 0,
    8 to 1,
    9 to 1,
    10 to 1
)

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}
