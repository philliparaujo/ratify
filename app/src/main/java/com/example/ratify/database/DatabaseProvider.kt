package com.example.ratify.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var instance: CountDatabase? = null

    fun getDatabase(context: Context): CountDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                CountDatabase::class.java,
                "counts.db"
            ).build()
            instance = newInstance
            newInstance
        }
    }
}