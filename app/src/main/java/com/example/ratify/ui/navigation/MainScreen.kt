package com.example.ratify.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val startDestination = HomeTarget

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController,
                startDestination
            )
        }
    ) { innerPadding ->
        NavigationHost(
            navController,
            startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}