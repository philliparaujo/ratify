package com.example.ratify.services

import androidx.core.app.NotificationCompat
import com.example.ratify.R

// Channel identifiers
const val CHANNEL_ID = "rating_channel"
const val CHANNEL_NAME = "Rating Channel"
const val NOTIFICATION_ID = 1

// Notification styles
val NOTIFICATION_ICON = R.drawable.ic_launcher_foreground
const val NOTIFICATION_PRIORITY = NotificationCompat.PRIORITY_MAX

// SharedPrefs names
const val BUTTON_SHARED_PREFS = "rating_button_prefs"

// GetExtra names
const val BUTTON_ID_EXTRA = "button_id"
const val CURRENT_RATING_EXTRA = "current_rating"

// TextView constants
val textViewId = R.id.currentRating
const val nullTextViewValue = "-"

// Button constants
val buttonIds = listOf(R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5,)
val defaultButtonValues = listOf(2, 4, 6, 8, 10)
