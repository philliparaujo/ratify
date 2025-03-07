package com.example.ratify.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    onDisabledClick: () -> Unit = {},
    enabled: Boolean = true,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonHeight = if (large) 54.dp else 40.dp
    val minWidth = if (large) 140.dp else 100.dp
    val typography = if (large) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .height(buttonHeight)
                .defaultMinSize(minWidth)
        ) {
            Text(
                text = text,
                style = typography,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
const val smallButtonText = "Delete"
const val bigButtonText = "Connect to Spotify"

@Preview(name = "Dark Enabled Button")
@Composable
fun DarkEnabledButtonPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MyButton(text = smallButtonText, onClick = {}, enabled = true)
            MyButton(text = smallButtonText, onClick = {}, onDisabledClick = { Log.d("Button", "Button is disabled") }, enabled = false)
            MyButton(text = bigButtonText, onClick = {}, enabled = true, large = true)
            MyButton(text = bigButtonText, onClick = {}, onDisabledClick = { Log.d("Button", "Button is disabled") }, enabled = false, large = true)
        }
    }
}

@Preview(name = "Light Enabled Button")
@Composable
fun LightEnabledButtonPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MyButton(text = smallButtonText, onClick = {}, enabled = true)
            MyButton(text = smallButtonText, onClick = {}, onDisabledClick = { Log.d("Button", "Button is disabled") }, enabled = false)
            MyButton(text = bigButtonText, onClick = {}, enabled = true, large = true)
            MyButton(text = bigButtonText, onClick = {}, onDisabledClick = { Log.d("Button", "Button is disabled") }, enabled = false, large = true)
        }
    }
}