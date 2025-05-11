package com.example.ratify.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ratify.core.helper.DialogSpecs
import com.example.ratify.core.helper.isLandscapeOrientation
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite

@Composable
fun GenericDialog(
    renderLandscapeContent: @Composable RowScope.() -> Unit,
    renderPortraitContent: @Composable ColumnScope.() -> Unit,
    onDismissRequest: () -> Unit,
) {
    val specs = if (isLandscapeOrientation()) DialogSpecs.LANDSCAPE else DialogSpecs.PORTRAIT
    val renderContent: @Composable BoxScope.() -> Unit = {
        if (isLandscapeOrientation()) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(specs.innerScopeSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                renderLandscapeContent()
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(specs.innerScopeSpacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                renderPortraitContent()
            }
        }
    }

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = specs.outerHorizontalPadding,
                    end = specs.outerHorizontalPadding,
                    top = specs.outerTopPadding,
                    bottom = specs.outerBottomPadding
                )
                .then(
                    if (isLandscapeOrientation()) Modifier.fillMaxHeight()
                    else Modifier.fillMaxWidth()
                )
                .background(MaterialTheme.colorScheme.surface)
                .padding(specs.innerPadding)
        ) {
            renderContent()
        }
    }
}

// Previews
@PreviewSuite
@Composable
fun GenericDialogPreviews() {
    MyPreview {
        GenericDialog(
            renderLandscapeContent = { Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxSize()) },
            renderPortraitContent = { Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxSize())}
        ) { }
    }
}