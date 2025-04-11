package com.example.ratify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.ratify.core.helper.NAVIGATION_TARGETS
import com.example.ratify.core.helper.START_NAVIGATION_TARGET

@Composable
fun MainScreen(
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    val navController = rememberNavController()

    NavigationRenderer(
        navController = navController,
        navigationTargets = NAVIGATION_TARGETS,
        onExportClick = onExportClick,
        onImportClick = onImportClick,
        startNavigationTarget = START_NAVIGATION_TARGET
    )
}