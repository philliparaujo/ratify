package com.example.ratify.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ratify.spotify.SpotifyViewModel
import com.example.ratify.ui.components.LeftNavDrawer
import com.example.ratify.ui.components.BottomNavBar

@Composable
fun NavigationRenderer(
    navController: NavHostController,
    startNavigationTarget: NavigationTarget,
    navigationTargets: List<NavigationTarget>,
    spotifyViewModel: SpotifyViewModel,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    // Figure out which Target is currently selected
    // Relies on composable<Target> route being a substring of Target.toString()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentTarget = navigationTargets.find {
        if (currentRoute != null) it.toString().contains(currentRoute) else false
    } ?: startNavigationTarget

    // Figure out current orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Shared attributes between both orientations
    val onClick = { target: NavigationTarget, isSelected: Boolean ->
        targetOnClick(navController, target, isSelected)
    }
    val navigationHost = @Composable { innerPadding: PaddingValues ->
        NavigationHost(
            navController,
            startNavigationTarget,
            modifier = Modifier.padding(innerPadding),
            spotifyViewModel,
            onExportClick = onExportClick,
            onImportClick = onImportClick
        )
    }

    // Render content
    if (isLandscape) {
        Scaffold() { innerPadding ->
            PermanentNavigationDrawer (
                drawerContent = {
                    LeftNavDrawer(
                        navigationTargets = navigationTargets,
                        currentTarget = currentTarget,
                        onClick = onClick
                    )
                }
            ) {
                navigationHost(innerPadding)
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    navigationTargets = navigationTargets,
                    currentNavigationTarget = currentTarget,
                    onClick = onClick
                )
            }
        ) { innerPadding ->
            navigationHost(innerPadding)
        }
    }
}

// Display the icon and label of the Target, for both orientations
@Composable
fun RenderTarget(
    target: NavigationTarget,
    isSelected: Boolean,
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = target.icon,
            contentDescription = target.title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = target.title,
            style = if (isSelected) MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
            else MaterialTheme.typography.labelLarge
        )
    }
}

// Navigation logic for clicking on the Target, for both orientations
fun targetOnClick(
    navController: NavHostController,
    navigationTarget: NavigationTarget,
    isSelected: Boolean,
    disabledClick: Boolean = false,
) {
    if (!isSelected && !disabledClick) {
        navController.navigate(navigationTarget) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}