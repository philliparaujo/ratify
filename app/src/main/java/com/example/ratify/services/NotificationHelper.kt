package com.example.ratify.services

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.ratify.core.model.Rating

fun flipButtonValue(value: Int): Int {
    return if (value % 2 == 0) value - 1 else value + 1
}
fun ratingToText(rating: Rating?): String {
    return (rating?.value ?: nullTextViewValue).toString()
}
fun setupNotification(context: Context, remoteViews: RemoteViews, onlyAlertOnce: Boolean = true): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(NOTIFICATION_ICON)
        .setCustomBigContentView(remoteViews)
        .setPriority(NOTIFICATION_PRIORITY)
        .setOngoing(true)
        .setOnlyAlertOnce(onlyAlertOnce)
}

fun Context.startRatingService(currentRating: Rating?) {
    val currentRatingText = ratingToText(currentRating)

    Intent(this, RatingService::class.java).apply {
        action = RatingService.Actions.START.toString()
        putExtra(CURRENT_RATING_EXTRA, currentRatingText)
    }.also {
        startService(it)
    }
}

fun Context.updateRatingService(currentRating: Rating?) {
    val currentRatingText = ratingToText(currentRating)

    Intent(this, RatingService::class.java).apply {
        action = RatingService.Actions.UPDATE.toString()
        putExtra(CURRENT_RATING_EXTRA, currentRatingText)
    }.also {
        startService(it)
    }
}

fun Context.stopRatingService() {
    Intent(this, RatingService::class.java).apply {
        action = RatingService.Actions.STOP.toString()
    }.also {
        startService(it)
    }
}