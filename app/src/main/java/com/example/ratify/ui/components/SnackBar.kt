package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun MySnackBar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snackbarData.visuals.message,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(modifier = Modifier.height(36.dp), contentAlignment = Alignment.Center) {
                snackbarData.visuals.actionLabel?.let { actionLabel ->
                    TextButton(
                        onClick = { snackbarData.performAction() }
                    ) {
                        Text(
                            text = actionLabel.uppercase(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } ?: Spacer(modifier = Modifier.width(64.dp))
            }
        }
    }
}



// Previews

@Preview(name = "SnackBar")
@Composable
fun SnackBarPreview() {
    RatifyTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MySnackBar(
                snackbarData = object : SnackbarData {
                    override val visuals = object : SnackbarVisuals {
                        override val message = "Preview"
                        override val actionLabel = "Dismiss"
                        override val withDismissAction = true
                        override val duration = SnackbarDuration.Short
                    }

                    override fun performAction() {}
                    override fun dismiss() {}
                }
            )

            MySnackBar(
                snackbarData = object : SnackbarData {
                    override val visuals = object : SnackbarVisuals {
                        override val message = "Preview"
                        override val actionLabel = null
                        override val withDismissAction = true
                        override val duration = SnackbarDuration.Short
                    }

                    override fun performAction() {}
                    override fun dismiss() {}
                }
            )
        }
    }
}

