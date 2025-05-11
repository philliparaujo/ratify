package com.example.ratify.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
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


    val padding = WindowInsets.systemBars.asPaddingValues()
    Log.d("MainActivity", padding.toString())

    NavigationRenderer(
        navController = navController,
        navigationTargets = NAVIGATION_TARGETS,
        onExportClick = onExportClick,
        onImportClick = onImportClick,
        startNavigationTarget = START_NAVIGATION_TARGET
    )
}