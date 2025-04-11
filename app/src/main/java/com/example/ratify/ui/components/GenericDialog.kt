package com.example.ratify.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ratify.core.helper.DialogSpecs
import com.example.ratify.mocks.LANDSCAPE_DEVICE
import com.example.ratify.mocks.MyPreview

@Composable
fun GenericDialog(
    renderLandscapeContent: @Composable RowScope.() -> Unit,
    renderPortraitContent: @Composable ColumnScope.() -> Unit,
    onDismissRequest: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val specs = if (isLandscape) DialogSpecs.LANDSCAPE else DialogSpecs.PORTRAIT

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        if (isLandscape) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = specs.outerHorizontalPadding,
                        vertical = specs.outerVerticalPadding
                    )
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(specs.innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(specs.innerScopeSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    renderLandscapeContent()
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = specs.outerHorizontalPadding,
                        vertical = specs.outerVerticalPadding
                    )
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(specs.innerPadding)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(specs.innerScopeSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    renderPortraitContent()
                }
            }
        }
    }
}

// Previews
@Preview(name = "Generic Dialog")
@Composable
fun GenericDialogPreview() {
    MyPreview(darkTheme = true) {
        Column {
            GenericDialog(
                renderLandscapeContent = { Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxSize()) },
                renderPortraitContent = { Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxSize())}
            ) { }
        }
    }
}

@Preview(name = "Landscape Generic Dialog", device = LANDSCAPE_DEVICE)
@Composable
fun LandscapeGenericDialogPreview() {
    MyPreview(darkTheme = true) {
        Column {
            GenericDialog(
                renderLandscapeContent = { Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxSize()) },
                renderPortraitContent = { Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxSize())}
            ) { }
        }
    }
}