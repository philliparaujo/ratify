package com.example.ratify.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ratify.spotifydatabase.Converters
import com.example.ratify.spotifydatabase.Song
import com.example.ratify.spotifydatabase.SongDao

@Database(
    entities = [Song::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class SongDatabase: RoomDatabase() {
    abstract val dao: SongDao
}