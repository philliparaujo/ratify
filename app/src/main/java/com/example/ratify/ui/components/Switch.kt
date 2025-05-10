package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ratify.core.helper.SwitchSpecs
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite

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
@PreviewSuite
@Composable
fun SwitchPreviews() {
    MyPreview {
        MySwitch(
            leftText = "Light Theme",
            rightText = "Dark Theme",
            checked = true
        )
    }
}