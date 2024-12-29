package com.example.ratify.database

import android.content.Context
import androidx.room.Room

object SongDatabaseProvider {
    @Volatile
    private var instance: SongDatabase? = null

    fun getDatabase(context: Context): SongDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                SongDatabase::class.java,
                "songs.db"
            ).build()
            instance = newInstance
            newInstance
        }
    }
}