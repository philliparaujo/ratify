package com.example.ratify.core.model

enum class SearchType(
    private val displayName: String
) {
    NAME("Name"),
    ARTISTS("Artists"),
    ALBUM("Album"),
    RATING("Rating");

    override fun toString(): String {
        return displayName
    }
}