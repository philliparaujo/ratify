package com.example.ratify.spotifydatabase

enum class LibrarySortType(
    private val displayName: String,
    val sortAscendingPreference: Boolean
) {
    LAST_PLAYED_TS("Last played",false),
    LAST_RATED_TS("Last rated", false),
    RATING("Rating", false),
    NAME("Name", true),
    TIMES_PLAYED("Times Played", false);

    override fun toString(): String {
        return displayName
    }
}