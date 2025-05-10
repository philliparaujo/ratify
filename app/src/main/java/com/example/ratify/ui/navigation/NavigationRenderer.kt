package com.example.ratify.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ratify.core.helper.isLandscapeOrientation
import com.example.ratify.di.LocalStateRepository
import com.example.ratify.repository.StateRepository
import com.example.ratify.ui.components.BottomNavBar
import com.example.ratify.ui.components.LeftNavDrawer
import com.example.ratify.ui.components.MySnackBar
import kotlinx.coroutines.launch

@Composable
fun NavigationRenderer(
    navController: NavHostController,
    startNavigationTarget: NavigationTarget,
    navigationTargets: List<NavigationTarget>,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    // Figure out which Target is currently selected
    // Relies on composable<Target> route being a substring of Target.toString()
    val currentTarget = navigationTargets.find {
        isRouteOnTarget(navController, it)
    } ?: startNavigationTarget

    // Shared attributes between both orientations
    val onClick = { target: NavigationTarget, isSelected: Boolean ->
        targetOnClick(navController, target, isSelected)
    }
    val navigationHost = @Composable { innerPadding: PaddingValues ->
        NavigationHost(
            navController,
            startNavigationTarget,
            modifier = Modifier.padding(innerPadding),
            onExportClick = onExportClick,
            onImportClick = onImportClick
        )
    }

    // Snackbar rendering
    val stateRepository: StateRepository = LocalStateRepository.current
    val snackbarHostState = stateRepository.snackbarHostState
    val scope = rememberCoroutineScope()

    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    // Render content: bottomBar in Portrait, leftNavDrawer in Landscape
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { snackbarData: SnackbarData ->
                MySnackBar(
                    snackbarData
                )
            }
        },
        bottomBar = { if (!isLandscapeOrientation()) {
            BottomNavBar(
                navigationTargets = navigationTargets,
                currentNavigationTarget = currentTarget,
                onClick = onClick
            )
        } },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.displayCutout)
    ) { innerPadding ->
        if (isLandscapeOrientation()) {
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
        } else {
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

// Checks if the currently selected route matches some target
@Composable
fun isRouteOnTarget(navController: NavHostController, target: NavigationTarget): Boolean {
    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val backStackEntry by navController.currentBackStackEntryAsState()
        return backStackEntry?.destination?.route
    }

    val route = currentRoute(navController) ?: return false

    return target.toString().contains(route)
}