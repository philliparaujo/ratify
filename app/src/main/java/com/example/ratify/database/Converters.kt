package com.example.ratify.database

import androidx.room.TypeConverter
import com.example.ratify.core.model.Rating
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist
import com.spotify.protocol.types.ImageUri

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromAlbum(album: Album?): String? {
        return album?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toAlbum(data: String?): Album? {
        return data?.let {
            gson.fromJson(it, object : TypeToken<Album>() {}.type)
        }
    }

    @TypeConverter
    fun fromArtist(artist: Artist?): String? {
        return artist?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toArtist(data: String?): Artist? {
        return data?.let {
            gson.fromJson(it, object : TypeToken<Artist>() {}.type)
        }
    }

    @TypeConverter
    fun fromArtistList(artists: List<Artist>?): String? {
        return artists?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toArtistList(data: String?): List<Artist>? {
        return data?.let {
            gson.fromJson(it, object : TypeToken<List<Artist>>() {}.type)
        }
    }

    @TypeConverter
    fun fromImageUri(imageUri: ImageUri?): String? {
        return imageUri?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toImageUri(data: String?): ImageUri? {
        return data?.let {
            gson.fromJson(it, object : TypeToken<ImageUri>() {}.type)
        }
    }

    @TypeConverter
    fun fromRating(rating: Rating?): Int? {
        return rating?.value
    }

    @TypeConverter
    fun toRating(value: Int?): Rating? {
        return value?.let { Rating(it) }
    }
}