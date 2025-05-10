package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.ratify.core.helper.SnackBarSpecs
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite

@Composable
fun MySnackBar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val specs = SnackBarSpecs

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = specs.outerHorizontalPadding,
                vertical = specs.outerVerticalPadding
            )
            .clip(RoundedCornerShape(specs.roundedCorner))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(
                horizontal = specs.innerHorizontalPadding,
                vertical = specs.innerVerticalPadding
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = snackbarData.visuals.message,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = specs.messageSize,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(modifier = Modifier.height(specs.height), contentAlignment = Alignment.Center) {
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
                }
            }
        }
    }
}

// Previews
@PreviewSuite
@Composable
fun SnackBarPreviews() {
    MyPreview {
        Column {
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

