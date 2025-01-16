package com.example.ratify.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ratify.ui.theme.RatifyTheme

@Composable
fun NavigationDrawer(
    navController: NavHostController,
    startTarget: Target,
    items: List<Target>,
    disabledClick: Boolean = false,
    content: @Composable () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentTarget = items.find {
        if (currentRoute != null) it.toString().contains(currentRoute) else false
    } ?: startTarget

    PermanentNavigationDrawer (
        drawerContent = {
            DrawerContent(
                navController,
                startTarget,
                items,
                currentTarget,
                disabledClick
            )
        }
    ) {
        content()
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    startTarget: Target,
    items: List<Target>,
    currentTarget: Target,
    disabledClick: Boolean,
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
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { target ->
                val isSelected = currentTarget == target

                NavigationDrawerItem(
                    icon = {
                        androidx.compose.foundation.layout.Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = target.icon,
                                contentDescription = target.title,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = target.title,
                                style = if (isSelected) MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                                else MaterialTheme.typography.labelLarge
                            )
                        }
                    },
                    label = { }, // Remove the label to avoid duplication
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
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            }
        }
    }
}

// Previews
@Preview(name = "Landscape Nav Drawer", widthDp = 640, heightDp = 360)
@Composable
fun LandscapeNavDrawerPreview() {
    RatifyTheme {
        Scaffold () { innerPadding ->
            NavigationDrawer(
                navController = rememberNavController(),
                startTarget = MusicTarget,
                items = listOf(MusicTarget, LibraryTarget, SettingsTarget),
                disabledClick = true,
            ) {
                Box(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}