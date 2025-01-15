package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.ratify.ui.components.TestDialog

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        var showDialog by remember { mutableStateOf(false) }

        Column {
            Text(text = "Settings Screen", fontSize = 24.sp)
            Button(
                onClick = { showDialog = true }
            ) {
                Text("Show dialog")
            }
        }

        if (showDialog) {
            TestDialog(
                onDismissRequest = { showDialog = false },
                name = ""
            )
        }
    }
}