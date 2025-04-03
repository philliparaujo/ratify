package com.example.ratify.spotifydatabase

enum class GroupType(
    val displayName: String
) {
    ARTIST("Artist"),
    ALBUM("Album");

    override fun toString(): String {
        return displayName
    }
}