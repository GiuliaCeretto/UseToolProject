package com.example.usetool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.usetool.navigation.NavRoutes

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = listOf(
        NavRoutes.Home.route,
        NavRoutes.Search.route,
        NavRoutes.Collegamento.route,
        NavRoutes.Consulenza.route,
        NavRoutes.Profilo.route,
        NavRoutes.Carrello.route
    )

    val routesWithoutTopBar = listOf(
        NavRoutes.Login.route,
        NavRoutes.Register.route,
        NavRoutes.SchedaStrumento.route,
        NavRoutes.SchedaDistributore.route
    )

    val showBottomBar = currentRoute in mainRoutes
    val showTopBar = currentRoute !in routesWithoutTopBar

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { if (showTopBar) AppTopBar(navController) },
        bottomBar = { if (showBottomBar) BottomNavBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            content(PaddingValues())
        }
    }
}