package com.example.ratify.database

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

// Abstracts launching Android activities to import/export Room database using system file pickers
class DatabaseIOHelper(
    private val activity: ComponentActivity,
    private val onExportComplete: (Boolean) -> Unit,
    private val onImportComplete: (Boolean) -> Unit
) {
    private val exportLauncher = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
        if (uri != null) {
            val success = SongDatabaseProvider.exportDatabase(activity, uri, activity.contentResolver)
            onExportComplete(success)
        } else {
            onExportComplete(false)
        }
    }
    private val importLauncher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val success = SongDatabaseProvider.importDatabase(activity, uri)
            onImportComplete(success)
        } else {
            onImportComplete(false)
        }
    }

    fun exportDatabase() {
        exportLauncher.launch(SongDatabaseProvider.DATABASE_NAME)
    }

    fun importDatabase() {
        importLauncher.launch(arrayOf("*/*"))
    }
}