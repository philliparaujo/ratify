package com.example.ratify.spotifydatabase

enum class SortType(val displayName: String) {
    LAST_PLAYED_TS("Last played"),
    LAST_RATED_TS("Last rated"),
    RATING("Rating"),
    NAME("Name"),
    TIMES_PLAYED("Times Played");

    override fun toString(): String {
        return displayName
    }
}