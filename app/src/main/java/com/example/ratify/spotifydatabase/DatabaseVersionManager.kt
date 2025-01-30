package com.example.ratify.spotifydatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseVersionManager {
    const val DATABASE_VERSION = 4

    // Migrations
    // Version 2: Added timesPlayed column
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE songs ADD COLUMN timesPlayed INTEGER DEFAULT 0 NOT NULL")
        }
    }

    // Version 3: Changed primary key from uri to (name, artists, duration)
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create new table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `songs_new` (
                    `album` TEXT NOT NULL, 
                    `artist` TEXT NOT NULL, 
                    `artists` TEXT NOT NULL, 
                    `duration` INTEGER NOT NULL, 
                    `imageUri` TEXT NOT NULL, 
                    `name` TEXT NOT NULL, 
                    `uri` TEXT NOT NULL, 
                    `lastPlayedTs` INTEGER, 
                    `timesPlayed` INTEGER NOT NULL, 
                    `lastRatedTs` INTEGER, 
                    `rating` INTEGER, 
                    PRIMARY KEY(`name`, `artists`, `duration`)
                );
            """.trimIndent())

            // Copy data from old table to new, ignoring inserts violating primary key uniqueness
            db.execSQL("""
                INSERT OR IGNORE INTO songs_new (album, artist, artists, duration, imageUri, name, uri, lastPlayedTs, timesPlayed, lastRatedTs, rating)
                SELECT album, artist, artists, duration, imageUri, name, uri, lastPlayedTs, timesPlayed, lastRatedTs, rating
                FROM songs;
            """.trimIndent())

            // Drop old table
            db.execSQL("DROP TABLE songs")

            // Rename new table to old
            db.execSQL("ALTER TABLE songs_new RENAME TO songs")
        }
    }

    // Version 4: Changed primary key from (name, artists, duration) to (name, artists)
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create new table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `songs_new` (
                    `album` TEXT NOT NULL, 
                    `artist` TEXT NOT NULL, 
                    `artists` TEXT NOT NULL, 
                    `duration` INTEGER NOT NULL, 
                    `imageUri` TEXT NOT NULL, 
                    `name` TEXT NOT NULL, 
                    `uri` TEXT NOT NULL, 
                    `lastPlayedTs` INTEGER, 
                    `timesPlayed` INTEGER NOT NULL, 
                    `lastRatedTs` INTEGER, 
                    `rating` INTEGER, 
                    PRIMARY KEY(`name`, `artists`)
                );
            """.trimIndent())

            // Copy data from old table to new, ignoring inserts violating primary key uniqueness
            db.execSQL("""
                INSERT OR IGNORE INTO songs_new (album, artist, artists, duration, imageUri, name, uri, lastPlayedTs, timesPlayed, lastRatedTs, rating)
                SELECT album, artist, artists, duration, imageUri, name, uri, lastPlayedTs, timesPlayed, lastRatedTs, rating
                FROM songs;
            """.trimIndent())

            // Drop old table
            db.execSQL("DROP TABLE songs")

            // Rename new table to old
            db.execSQL("ALTER TABLE songs_new RENAME TO songs")
        }
    }

    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4
        )
    }
}