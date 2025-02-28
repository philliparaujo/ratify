package com.example.ratify.ui.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null
)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)

object SnackbarController {
    private val _events = Channel<SnackbarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}

fun showSnackbar(
    scope : CoroutineScope,
    message: String,
    action: SnackbarAction? = null
) {
    scope.launch {
        SnackbarController.sendEvent(
            event = SnackbarEvent(
                message = message,
                action = action
            )
        )
    }
}