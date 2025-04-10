package com.example.ratify.core.model

enum class GroupType(
    private val displayName: String
) {
    ARTIST("Artist"),
    ALBUM("Album");

    override fun toString(): String {
        return displayName
    }
}