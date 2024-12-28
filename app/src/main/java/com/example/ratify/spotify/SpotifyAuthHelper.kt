package com.example.ratify.spotify

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyAuthHelper(
    private val activity: ComponentActivity,
    private val spotifyViewModel: SpotifyViewModel
) {
    private val authLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val response = AuthorizationClient.getResponse(result.resultCode, data)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d("SpotifyAuthHelper", "Authorization successful!")
                    spotifyViewModel.onEvent(SpotifyEvent.ConnectSpotify)
                }
                AuthorizationResponse.Type.ERROR -> {
                    Log.e("SpotifyAuthHelper", "Auth error: ${response.error}")
                }
                else -> {
                    Log.e("SpotifyAuthHelper", "Auth flow cancelled")
                }
            }
        }

    fun launchAuth(request: AuthorizationRequest) {
        val authIntent = AuthorizationClient.createLoginActivityIntent(activity, request)
        authLauncher.launch(authIntent)
    }
}