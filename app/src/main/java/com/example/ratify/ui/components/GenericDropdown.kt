package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.mocks.MyPreview

@Composable
fun <T> GenericDropdown(
    options: List<T>,
    onSelect: (T) -> Unit,
    renderText: @Composable (T) -> Unit,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { setExpanded(false) },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = {
                    renderText(option)
                },
                onClick = {
                    onSelect(option)
                    setExpanded(false)
                },
                colors = MenuItemColors(
                    textColor = MaterialTheme.colorScheme.onBackground,
                    leadingIconColor = MaterialTheme.colorScheme.onBackground,
                    trailingIconColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}