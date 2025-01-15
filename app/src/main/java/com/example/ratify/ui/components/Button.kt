package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    large: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonHeight = if (large) 54.dp else 40.dp
    val minWidth = if (large) 140.dp else 100.dp

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .height(buttonHeight)
            .defaultMinSize(minWidth)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Previews
val smallButtonText = "Delete"
val bigButtonText = "Connect to Spotify"

@Preview(name = "Enabled Button")
@Composable
fun EnabledButtonPreview() {
    RatifyTheme(darkTheme = true) {
        Column {
            MyButton(text = smallButtonText, onClick = {}, enabled = true)
            MyButton(text = smallButtonText, onClick = {}, enabled = false)
            MyButton(text = bigButtonText, onClick = {}, enabled = true, large = true)
            MyButton(text = bigButtonText, onClick = {}, enabled = false, large = true)
        }
    }
}