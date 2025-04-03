package com.example.ratify.spotifydatabase

enum class SearchType(
    val displayName: String
) {
    NAME("Name"),
    ARTISTS("Artists"),
    ALBUM("Album"),
    RATING("Rating");

    override fun toString(): String {
        return displayName
    }
}