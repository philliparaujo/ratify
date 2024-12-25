package com.example.ratify

sealed interface CountEvent {
    data class IncrementCount(val count: Count): CountEvent
    data class DecrementCount(val count: Count): CountEvent
    data class UpdateValue(val count: Count, val newValue: Int): CountEvent
    object CreateCount: CountEvent
    data class DeleteCount(val count: Count): CountEvent

}