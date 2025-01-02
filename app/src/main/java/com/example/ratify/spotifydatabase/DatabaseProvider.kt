package com.example.ratify.database

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.Room
import com.example.ratify.MainActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object SongDatabaseProvider {
    @Volatile
    private var instance: SongDatabase? = null
    private val databaseName = "songs.db"

    fun getDatabase(context: Context): SongDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                SongDatabase::class.java,
                databaseName
            ).build()
            instance = newInstance
            newInstance
        }
    }

    private fun closeDatabase() {
        instance?.close()
        instance = null
    }

    fun forceDatabaseSync(database: SongDatabase) {
        database.openHelper.writableDatabase.close()
    }

    fun exportDatabase(context: Context, destinationUri: Uri, contentResolver: ContentResolver): Boolean {
        return try {
            val database = getDatabase(context)
            forceDatabaseSync(database) // Ensure changes are written to disk

            val databasePath = context.getDatabasePath(databaseName).absolutePath
            val inputFile = File(databasePath)
            val outputStream = contentResolver.openOutputStream(destinationUri) ?: return false

            FileInputStream(inputFile).use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importDatabase(context: Context, sourceUri: Uri): Boolean {
        val destinationPath = context.getDatabasePath(databaseName).absolutePath
        Log.d("SongDatabaseProvider", "Importing database from URI: $sourceUri to $destinationPath")

        return try {
            // Close the existing database connection
            closeDatabase()

            // Perform file copy
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationPath).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("SongDatabaseProvider", "Database import completed successfully.")

            // Restart the app to reflect new database state
            (context as? Activity)?.runOnUiThread {
                (context as MainActivity).restartApp()
            }

            true
        } catch (e: Exception) {
            Log.e("SongDatabaseProvider", "Error during database import: ${e.message}")
            false
        }
    }
}