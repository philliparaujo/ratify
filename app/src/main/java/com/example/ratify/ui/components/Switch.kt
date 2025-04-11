package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ratify.core.helper.SwitchSpecs
import com.example.ratify.mocks.MyPreview

@Composable
fun MySwitch(
    leftText: String,
    rightText: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val specs = SwitchSpecs

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(specs.spacing),
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
const val leftText = "Light Theme"
const val rightText = "Dark Theme"

@Preview(name = "Dark Switch")
@Composable
fun DarkSwitchPreview() {
    MyPreview(darkTheme = true) {
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
    MyPreview(darkTheme = false) {
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