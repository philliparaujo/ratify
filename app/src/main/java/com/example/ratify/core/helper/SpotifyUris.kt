package com.example.ratify.core.helper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

val spotifyPackageName = "com.spotify.music"
val playStorePackageName = "com.android.vending"

val playStoreUri = "market://details?id=$spotifyPackageName"
val alternativePlayStoreUri = "https://play.google.com/store/apps/details?id=$spotifyPackageName"

fun navigateToSpotifyInstall(context: Context) {
    val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(playStoreUri)
        setPackage(playStorePackageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(alternativePlayStoreUri)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(playStoreIntent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(fallbackIntent)
    }
}