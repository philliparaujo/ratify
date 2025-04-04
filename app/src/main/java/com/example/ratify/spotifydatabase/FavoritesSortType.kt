package com.example.ratify.spotifydatabase

enum class FavoritesSortType(
    private val displayName: String,
    val sortAscendingPreference: Boolean
) {
    LAST_PLAYED_TS("Last played",false),
    LAST_RATED_TS("Last rated", false),
    RATING("Rating", false),
    NAME("Name", true),
    TIMES_PLAYED("Times Played", false),
    NUM_ENTRIES("Entries", false);

    override fun toString(): String {
        return displayName
    }
}