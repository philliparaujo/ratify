package com.example.ratify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CountViewModel(
    private val dao: CountDao
): ViewModel() {
    private val _state = MutableStateFlow(CountState())
    val state: StateFlow<CountState> = _state  // Read-only state

    init {
        viewModelScope.launch {
            dao.get().collect { counts ->
                _state.value = CountState(counts = counts)
            }
        }
    }

    fun onEvent(event: CountEvent) {
        when (event) {
            is CountEvent.IncrementCount -> {
                val count = event.count
                updateDatabaseAndState(count, count.value + 1)
            }
            is CountEvent.DecrementCount -> {
                val count = event.count
                updateDatabaseAndState(count, count.value - 1)
            }
            is CountEvent.UpdateValue -> {
                updateDatabaseAndState(event.count, event.newValue)
            }
            CountEvent.CreateCount -> {
                insertIntoDatabaseAndState(Count(99))
            }
            is CountEvent.DeleteCount -> {
                deleteFromDatabaseAndState(event.count)
            }
        }
    }

    private fun updateDatabaseAndState(count: Count, newValue: Int) {
        viewModelScope.launch {
            val newCount = Count(newValue, count.id)
            dao.update(newCount)

            // Keep all Count objects the same unless it's the modified one
            _state.value = state.value.copy(
                counts = _state.value.counts.map {
                    if (it.id == count.id) newCount else it
                }
            )
        }
    }
    private fun insertIntoDatabaseAndState(count: Count) {
        viewModelScope.launch {
            dao.insert(count)
            _state.value = state.value.copy(
                counts = _state.value.counts + count
            )
        }
    }
    private fun deleteFromDatabaseAndState(count: Count) {
        viewModelScope.launch {
            dao.delete(count)
            _state.value = state.value.copy(
                counts = _state.value.counts.filter { it.id != count.id }
            )
        }
    }
}