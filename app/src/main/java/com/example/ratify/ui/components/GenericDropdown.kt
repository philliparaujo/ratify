package com.example.ratify.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> GenericDropdown(
    options: List<T>,
    onSelect: (T) -> Unit,
    renderText: @Composable (T) -> Unit,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
)
{
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