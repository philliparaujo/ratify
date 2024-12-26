package com.example.ratify.ui

import android.content.Context
import com.example.ratify.database.DatabaseProvider

object CountViewModelProvider {
    fun provideCountViewModel(context: Context): CountViewModel {
        val db = DatabaseProvider.getDatabase(context)
        return CountViewModel(db.count)
    }
}