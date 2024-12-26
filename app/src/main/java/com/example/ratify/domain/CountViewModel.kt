package com.example.ratify.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ratify.database.Count
import com.example.ratify.database.CountDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CountViewModel(
    private val dao: CountDao
): ViewModel() {
    val counts: Flow<List<Count>> = dao.get()

    fun onEvent(event: CountEvent) {
        when (event) {
            is CountEvent.IncrementCount -> {
                val count = event.count
                updateCount(count, count.value + 1)
            }
            is CountEvent.DecrementCount -> {
                val count = event.count
                updateCount(count, count.value - 1)
            }
            is CountEvent.UpdateValue -> {
                updateCount(event.count, event.newValue)
            }
            CountEvent.CreateCount -> {
                insertCount(Count(99))
            }
            is CountEvent.DeleteCount -> {
                deleteCount(event.count)
            }
        }
    }

    private fun updateCount(count: Count, newValue: Int) {
        viewModelScope.launch {
            dao.update(count.copy(value = newValue))
        }
    }
    private fun insertCount(count: Count) {
        viewModelScope.launch {
            dao.insert(count)
        }
    }
    private fun deleteCount(count: Count) {
        viewModelScope.launch {
            dao.delete(count)
        }
    }
}