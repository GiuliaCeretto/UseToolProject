package com.example.usetool.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoutes.Home.route,
        NavRoutes.Search.route,
        NavRoutes.Collegamento.route,
        NavRoutes.Consulenza.route,
        NavRoutes.Profilo.route,
        NavRoutes.Carrello.route
    )

    val showTopBar = currentRoute != NavRoutes.Login.route

    Scaffold(
        topBar = {
            if (showTopBar) {
                AppTopBar(navController = navController)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->
        content(padding)
    }
}


