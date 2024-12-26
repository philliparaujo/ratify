package com.example.ratify.ui.navigation

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(navController: NavHostController, startDestination: Destination) {
    val items = listOf(HomeTarget, ProfileTarget, SettingsTarget, CountManagerTarget)
    var currentTarget by remember { mutableStateOf(startDestination) }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        items.forEach { target ->
            NavigationBarItem(
                icon = {
                    Icon(target.icon, contentDescription = target.title)
                },
                label = { Text(target.title) },
                selected = currentTarget == target,
                onClick = {
                    if (currentTarget != target) {
                        navController.navigate(target) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                        currentTarget = target
                    }
                }
            )
        }
    }
}