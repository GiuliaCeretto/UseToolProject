package com.example.usetool.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(navController = navController)
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { padding ->
        content(padding)
    }
}

