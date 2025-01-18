package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.R
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun MyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonSize = if (large) 80.dp else 50.dp
    val iconSize = if (large) 50.dp else 30.dp

    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .size(buttonSize)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "foo",
            modifier = Modifier
                .size(iconSize)
        )
    }
}

// Previews
@Preview(name = "Enabled Icon Button")
@Composable
fun EnabledIconButtonPreview() {
    RatifyTheme(darkTheme = true) {
        Column {
            MyIconButton(
                onClick = {},
                enabled = true,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = false
            )
            MyIconButton(
                onClick = {},
                enabled = false,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = false
            )
            MyIconButton(
                onClick = {},
                enabled = true,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = true
            )
            MyIconButton(
                onClick = {},
                enabled = false,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = true
            )
        }
    }
}