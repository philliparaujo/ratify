package com.example.ratify.domain

import com.example.ratify.database.Count

data class CountState(
    val counts: List<Count> = emptyList()
)
