package com.example.ratify.ui.components

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ratify.mocks.Preview
import com.example.ratify.ui.navigation.LibraryNavigationTarget
import com.example.ratify.ui.navigation.MusicNavigationTarget
import com.example.ratify.ui.navigation.NavigationTarget
import com.example.ratify.ui.navigation.RenderTarget
import com.example.ratify.ui.navigation.SettingsNavigationTarget

@Composable
fun BottomNavBar(
    navigationTargets: List<NavigationTarget>,
    currentNavigationTarget: NavigationTarget,
    onClick: (NavigationTarget, Boolean) -> Unit
) {
    val outlineColor = MaterialTheme.colorScheme.secondary
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.drawBehind {
            // Top gray outline of nav bar
            drawLine(
                color = outlineColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        },
    ) {
        navigationTargets.forEach { target ->
            val isSelected = currentNavigationTarget == target

            NavigationBarItem(
                icon = { RenderTarget(target, isSelected) },
                label = { }, // Remove the label to avoid duplication
                selected = isSelected,
                onClick = { onClick(target, isSelected) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    }
}

// Previews
@Preview(name = "Dark Portrait Bottom Nav", widthDp = 360, heightDp = 640)
@Composable
fun DarkPortraitBottomNavPreview() {
    Preview(darkTheme = true) {
        BottomNavBar(
            onClick = { _, _ -> run {} },
            navigationTargets = listOf(MusicNavigationTarget, LibraryNavigationTarget, SettingsNavigationTarget),
            currentNavigationTarget = MusicNavigationTarget,
        )
    }
}

@Preview(name = "Light Portrait Bottom Nav", widthDp = 360, heightDp = 640)
@Composable
fun LightPortraitBottomNavPreview() {
    Preview(darkTheme = false) {
        BottomNavBar(
            onClick = { _, _ -> run {} },
            navigationTargets = listOf(MusicNavigationTarget, LibraryNavigationTarget, SettingsNavigationTarget),
            currentNavigationTarget = MusicNavigationTarget,
        )
    }
}