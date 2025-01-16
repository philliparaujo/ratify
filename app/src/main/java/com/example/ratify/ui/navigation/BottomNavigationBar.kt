package com.example.ratify.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    startTarget: Target,
    items: List<Target>,
    disabledClick: Boolean = false,
) {
    // Relies on composable<Target> route being a substring of Target.toString()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentTarget = items.find {
        if (currentRoute != null) it.toString().contains(currentRoute) else false
    } ?: startTarget

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
        }
    ) {
        items.forEach { target ->
            val isSelected = currentTarget == target

            NavigationBarItem(
                icon = {
                    Icon(target.icon, contentDescription = target.title)
                },
                label = {
                    Text(
                        text = target.title,
                        style = if (isSelected) MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                                else MaterialTheme.typography.labelLarge
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentTarget != target && !disabledClick) {
                        navController.navigate(target) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.background,
                )
            )
        }
    }
}

// Previews
@Preview(name = "Portrait Bottom Nav", widthDp = 360, heightDp = 640)
@Composable
fun PortraitBottomNavPreview() {
    RatifyTheme {
        Scaffold (
            bottomBar = {
                BottomNavigationBar(
                    navController = rememberNavController(),
                    startTarget = MusicTarget,
                    items = listOf(MusicTarget, LibraryTarget, SettingsTarget),
                    disabledClick = true,
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))
        }
    }
}