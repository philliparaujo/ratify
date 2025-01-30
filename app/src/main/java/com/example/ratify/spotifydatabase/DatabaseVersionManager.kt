package com.example.ratify.spotifydatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseVersionManager {
    const val DATABASE_VERSION = 2

    // Migrations
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE songs ADD COLUMN timesPlayed INTEGER DEFAULT 0 NOT NULL")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
        }
    }

    fun getAllMigrations(): Array<Migration> {
        return arrayOf(
            MIGRATION_1_2,
//            MIGRATION_2_3
        )
    }
}