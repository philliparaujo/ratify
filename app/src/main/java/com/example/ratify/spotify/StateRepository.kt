package com.example.ratify.spotify

import com.example.ratify.core.model.Rating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: find a better location for this class
class StateRepository {
    private val _currentRating = MutableStateFlow<Rating?>(null)
    val currentRating: StateFlow<Rating?> = _currentRating

    fun updateCurrentRating(rating: Rating?) {
        _currentRating.value = rating
    }
}