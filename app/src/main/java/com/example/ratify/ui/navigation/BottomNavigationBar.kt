package com.example.ratify.ui.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController, startDestination: Destination) {
    val items = listOf(HomeTarget, ProfileTarget, SettingsTarget, CountManagerTarget)

    // Relies on composable<Target> route being a substring of Target.toString()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentTarget = items.find {
        if (currentRoute != null) it.toString().contains(currentRoute) else false
    } ?: startDestination

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    if (currentTarget != target) {
                        navController.navigate(target) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}