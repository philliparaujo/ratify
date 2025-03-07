package com.example.ratify.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.ui.navigation.LibraryNavigationTarget
import com.example.ratify.ui.navigation.MusicNavigationTarget
import com.example.ratify.ui.navigation.NavigationTarget
import com.example.ratify.ui.navigation.RenderTarget
import com.example.ratify.ui.navigation.SettingsNavigationTarget
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun LeftNavDrawer(
    navigationTargets: List<NavigationTarget>,
    currentTarget: NavigationTarget,
    onClick: (NavigationTarget, Boolean) -> Unit
) {
    val outlineColor = MaterialTheme.colorScheme.secondary
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .width(100.dp)
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
                    label = { }, // Remove the label to avoid duplication
                    selected = isSelected,
                    onClick = { onClick(target, isSelected) },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.background,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }
        }
    }
}

// Previews
@Preview(name = "Dark Landscape Nav Drawer", widthDp = 640, heightDp = 360)
@Composable
fun DarkLandscapeNavDrawerPreview() {
    RatifyTheme(
        darkTheme = true
    ) {
        LeftNavDrawer(
            onClick = { _, _ -> run {} },
            navigationTargets = listOf(MusicNavigationTarget, LibraryNavigationTarget, SettingsNavigationTarget),
            currentTarget = MusicNavigationTarget
        )
    }
}

@Preview(name = "Light Landscape Nav Drawer", widthDp = 640, heightDp = 360)
@Composable
fun LightLandscapeNavDrawerPreview() {
    RatifyTheme(
        darkTheme = false
    ) {
        LeftNavDrawer(
            onClick = { _, _ -> run {} },
            navigationTargets = listOf(MusicNavigationTarget, LibraryNavigationTarget, SettingsNavigationTarget),
            currentTarget = MusicNavigationTarget
        )
    }
}