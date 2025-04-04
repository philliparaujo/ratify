package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun MySwitch(
    leftText: String,
    rightText: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Text(
            leftText,
            color = MaterialTheme.colorScheme.onBackground
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(
            rightText,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Previews
val leftText = "Light Theme"
val rightText = "Dark Theme"

@Preview(name = "Dark Switch")
@Composable
fun DarkSwitchPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MySwitch(
                leftText = leftText,
                rightText = rightText,
                checked = true
            )
        }
    }
}

@Preview(name = "Light Switch")
@Composable
fun LightSwitchPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MySwitch(
                leftText = leftText,
                rightText = rightText,
                checked = true
            )
        }
    }
}