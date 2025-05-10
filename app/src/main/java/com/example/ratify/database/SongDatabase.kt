package com.example.ratify.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Represents the Room database schema, tying the Song entity and its DAO together
@Database(
    entities = [Song::class],
    version = DatabaseVersionManager.DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SongDatabase: RoomDatabase() {
    abstract val dao: SongDao
}