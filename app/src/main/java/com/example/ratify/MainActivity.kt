package com.example.ratify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.ratify.ui.theme.RatifyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            CountDatabase::class.java,
            "counts.db"
        ).build()
    }
    private val viewModel by viewModels<CountViewModel>
    {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CountViewModel(db.count) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            val count = db.count
            val numEntries = count.numEntries().firstOrNull() ?: 0
            if (numEntries == 0) {
                count.insert(Count(99))
            }
        }

        setContent {
            RatifyTheme {
                val state by viewModel.state.collectAsState()
                CountScreen(state, viewModel::onEvent)
            }
        }
    }
}