package com.example.ratify.spotifydatabase

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class Rating(
    @JsonProperty("value")
    @SerializedName("value")
    val value: Int
) {
    init {
        require(value in 1..10) { "Rating must be between 1 and 10" }
    }

    override fun toString(): String {
        return "Rating(value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Rating) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        fun from(value: Int): Rating {
            return Rating(value)
        }
    }
}