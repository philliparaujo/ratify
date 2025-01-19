package com.example.ratify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.ratify.ui.components.MyButton

@Composable
fun SettingsScreen(
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            Text(text = "Settings Screen", fontSize = 24.sp)
            Row {
                MyButton(
                    onClick = {
                        onExportClick.invoke()
                    },
                    text = "Export Database"
                )
                MyButton(
                    onClick = {
                        onImportClick.invoke()
                    },
                    text = "Import Database"
                )
            }
        }
    }
}