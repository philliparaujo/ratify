package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.ratify.core.helper.LeftNavBarSpecs
import com.example.ratify.core.helper.NoRippleInteractionSource
import com.example.ratify.mocks.MyPreview
import com.example.ratify.mocks.PreviewSuite
import com.example.ratify.ui.navigation.FavoritesNavigationTarget
import com.example.ratify.ui.navigation.LibraryNavigationTarget
import com.example.ratify.ui.navigation.MusicNavigationTarget
import com.example.ratify.ui.navigation.NavigationTarget
import com.example.ratify.ui.navigation.RenderTarget
import com.example.ratify.ui.navigation.SettingsNavigationTarget

@Composable
fun LeftNavDrawer(
    navigationTargets: List<NavigationTarget>,
    currentTarget: NavigationTarget,
    onClick: (NavigationTarget, Boolean) -> Unit
) {
    val outlineColor = MaterialTheme.colorScheme.secondary
    val specs = LeftNavBarSpecs

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .width(specs.width)
            .drawBehind {
                // Right gray outline of nav bar
                drawLine(
                    color = outlineColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            navigationTargets.forEach { target ->
                val isSelected = currentTarget == target

                NavigationDrawerItem(
                    icon = { RenderTarget(target, isSelected) },
                    label = { }, // Remove the label to avoid duplication (RenderTarget)
                    selected = isSelected,
                    onClick = { onClick(target, isSelected) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.background,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    interactionSource = NoRippleInteractionSource
                )
            }
        }
    }
}

// Previews
@PreviewSuite
@Composable
fun LeftNavDrawerPreviews() {
    MyPreview {
        LeftNavDrawer(
            onClick = { _, _ -> run {} },
            navigationTargets = listOf(
                MusicNavigationTarget,
                LibraryNavigationTarget,
                FavoritesNavigationTarget,
                SettingsNavigationTarget
            ),
            currentTarget = MusicNavigationTarget
        )
    }
}