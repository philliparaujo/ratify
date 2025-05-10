package com.example.ratify.ui.components

import android.util.Log
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
import com.example.ratify.core.helper.ButtonSpecs
import com.example.ratify.core.helper.textStyle
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite

@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    onDisabledClick: () -> Unit = {},
    enabled: Boolean = true,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val specs = if (large) ButtonSpecs.LARGE else ButtonSpecs.SMALL

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
                .height(specs.height)
                .defaultMinSize(specs.width)
        ) {
            Text(
                text = text,
                style = specs.textStyle(),
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
@PreviewSuite
@Composable
fun ButtonPreviews() {
    MyPreview {
        Column {
            MyButton(text = "Delete", onClick = {}, enabled = true)
            MyButton(text = "Delete", onClick = {}, onDisabledClick = { Log.d("Button", "Button is disabled") }, enabled = false)
            MyButton(text = "Connect to Spotify", onClick = {}, enabled = true, large = true)
            MyButton(text = "Connect to Spotify", onClick = {}, onDisabledClick = { Log.d("Button", "Button is disabled") }, enabled = false, large = true)
        }
    }
}