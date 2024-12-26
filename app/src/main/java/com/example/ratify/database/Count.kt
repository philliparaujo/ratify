package com.example.ratify.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("count")
data class Count(
    val value: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
