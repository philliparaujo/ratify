package com.example.ratify.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.ratify.R
import com.example.ratify.core.helper.IconButtonSpecs
import com.example.ratify.mocks.MyPreview

@Composable
fun MyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    onDisabledClick: () -> Unit = {},
    enabled: Boolean = true,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val specs = if (large) IconButtonSpecs.LARGE else IconButtonSpecs.SMALL

    Box {
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
                .size(specs.buttonSize)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "foo",
                modifier = Modifier.size(specs.iconSize)
            )
        }

        // Overlay to capture clicks when disabled
        if (!enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        enabled = true,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onDisabledClick()
                    }
            )
        }
    }
}

// Previews
@Preview(name = "Dark Enabled Icon Button")
@Composable
fun DarkEnabledIconButtonPreview() {
    MyPreview(darkTheme = true) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MyIconButton(
                onClick = {},
                enabled = true,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = false
            )
            MyIconButton(
                onClick = {},
                onDisabledClick = { Log.d("IconButton", "IconButton is disabled") },
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
                onDisabledClick = { Log.d("IconButton", "IconButton is disabled") },
                enabled = false,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = true
            )
        }
    }
}

@Preview(name = "Light Enabled Icon Button")
@Composable
fun LightEnabledIconButtonPreview() {
    MyPreview(darkTheme = false) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MyIconButton(
                onClick = {},
                enabled = true,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = false
            )
            MyIconButton(
                onClick = {},
                onDisabledClick = { Log.d("IconButton", "IconButton is disabled") },
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
                onDisabledClick = { Log.d("IconButton", "IconButton is disabled") },
                enabled = false,
                icon = ImageVector.vectorResource(id = R.drawable.baseline_pause_24),
                large = true
            )
        }
    }
}