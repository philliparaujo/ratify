package com.example.ratify.services

import com.example.ratify.R

const val CHANNEL_ID = "rating_channel"
const val CHANNEL_NAME = "Rating Channel"
const val NOTIFICATION_ID = 1

const val BUTTON_SHARED_PREFS = "rating_button_prefs"

const val BUTTON_ID_EXTRA = "button_id"
const val CURRENT_RATING_EXTRA = "current_rating"

val textViewId = R.id.currentRating

val buttonIds = listOf(R.id.button2, R.id.button4, R.id.button6, R.id.button8, R.id.button10)
val defaultButtonValues = listOf(2, 4, 6, 8, 10)

const val nullTextViewValue = "-"