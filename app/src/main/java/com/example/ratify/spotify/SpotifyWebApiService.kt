package com.example.ratify.spotify

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Retrofit interface for Spotify Web API endpoints
 */
interface SpotifyWebApi {
    @GET("v1/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): SpotifyUser

    @POST("v1/users/{user_id}/playlists")
    suspend fun createPlaylist(
        @Path("user_id") userId: String,
        @Header("Authorization") authorization: String,
        @Body request: CreatePlaylistRequest
    ): PlaylistResponse

    @POST("v1/playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylist(
        @Path("playlist_id") playlistId: String,
        @Header("Authorization") authorization: String,
        @Body request: AddTracksRequest
    ): AddTracksResponse
}

/**
 * Data models for API requests and responses
 */
data class SpotifyUser(
    val id: String,
    val display_name: String?,
    val email: String?
)

data class CreatePlaylistRequest(
    val name: String,
    val public: Boolean = false,
    val description: String? = null
)

data class PlaylistResponse(
    val id: String,
    val name: String,
    val external_urls: ExternalUrls
)

data class ExternalUrls(
    val spotify: String
)

data class AddTracksRequest(
    val uris: List<String>
)

data class AddTracksResponse(
    val snapshot_id: String
)

/**
 * Service class for Spotify Web API operations
 */
class SpotifyWebApiService(private val accessToken: String?) {
    private val api: SpotifyWebApi

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(SpotifyWebApi::class.java)
    }

    /**
     * Get the current user's Spotify ID
     */
    suspend fun getCurrentUserId(): String? {
        return try {
            val token = accessToken ?: run {
                Log.e("SpotifyWebApiService", "No access token available")
                return null
            }
            val user = api.getCurrentUser("Bearer $token")
            user.id
        } catch (e: Exception) {
            Log.e("SpotifyWebApiService", "Error getting user ID: ${e.message}", e)
            null
        }
    }

    /**
     * Create a new playlist for the user
     */
    suspend fun createPlaylist(userId: String, playlistName: String): String? {
        return try {
            val token = accessToken ?: run {
                Log.e("SpotifyWebApiService", "No access token available")
                return null
            }
            val request = CreatePlaylistRequest(
                name = playlistName,
                public = false,
                description = "Created with Ratify"
            )
            val response = api.createPlaylist(userId, "Bearer $token", request)
            Log.d("SpotifyWebApiService", "Playlist created: ${response.name} (${response.id})")
            response.id
        } catch (e: Exception) {
            Log.e("SpotifyWebApiService", "Error creating playlist: ${e.message}", e)
            null
        }
    }

    /**
     * Add tracks to a playlist
     * Spotify limits to 100 tracks per request, so this should be called in batches
     */
    suspend fun addTracksToPlaylist(playlistId: String, trackUris: List<String>): Boolean {
        return try {
            val token = accessToken ?: run {
                Log.e("SpotifyWebApiService", "No access token available")
                return false
            }
            val request = AddTracksRequest(uris = trackUris)
            val response = api.addTracksToPlaylist(playlistId, "Bearer $token", request)
            Log.d("SpotifyWebApiService", "Added ${trackUris.size} tracks to playlist $playlistId")
            true
        } catch (e: Exception) {
            Log.e("SpotifyWebApiService", "Error adding tracks to playlist: ${e.message}", e)
            false
        }
    }

    /**
     * Create a playlist and add all tracks to it
     * Handles batching if there are more than 100 tracks
     */
    suspend fun createPlaylistWithTracks(
        userId: String,
        playlistName: String,
        trackUris: List<String>
    ): PlaylistCreationResult {
        // Create the playlist
        val playlistId = createPlaylist(userId, playlistName)
            ?: return PlaylistCreationResult.Error("Failed to create playlist")

        if (trackUris.isEmpty()) {
            return PlaylistCreationResult.Success(playlistId, playlistName)
        }

        // Add tracks in batches of 100 (Spotify's limit)
        val batches = trackUris.chunked(100)
        var successfulBatches = 0

        for (batch in batches) {
            val success = addTracksToPlaylist(playlistId, batch)
            if (success) {
                successfulBatches++
            } else {
                Log.w("SpotifyWebApiService", "Failed to add batch to playlist")
            }
        }

        return if (successfulBatches == batches.size) {
            PlaylistCreationResult.Success(playlistId, playlistName)
        } else {
            PlaylistCreationResult.PartialSuccess(
                playlistId,
                playlistName,
                "Added $successfulBatches of ${batches.size} batches"
            )
        }
    }
}

/**
 * Sealed class representing the result of playlist creation
 */
sealed class PlaylistCreationResult {
    data class Success(val playlistId: String, val playlistName: String) : PlaylistCreationResult()
    data class PartialSuccess(val playlistId: String, val playlistName: String, val message: String) : PlaylistCreationResult()
    data class Error(val message: String) : PlaylistCreationResult()
}
