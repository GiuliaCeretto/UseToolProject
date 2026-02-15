package com.example.usetool.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // NECESSARIO per 'by'
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState // IMPORT MANCANTE
import com.example.usetool.navigation.NavRoutes // Assicurati che il package sia corretto

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    // Osserva il backstack per sapere dove si trova l'utente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definiamo le rotte principali che richiedono la BottomBar
    val mainRoutes = listOf(
        NavRoutes.Home.route,
        NavRoutes.Search.route,
        NavRoutes.Collegamento.route,
        NavRoutes.Consulenza.route,
        NavRoutes.Profilo.route,
        NavRoutes.Carrello.route
    )

    // Logica di visibilità UI
    val showBottomBar = currentRoute in mainRoutes
    val showTopBar = currentRoute != NavRoutes.Login.route && currentRoute != NavRoutes.Register.route

    Scaffold(
        topBar = {
            if (showTopBar) {
                AppTopBar(navController = navController)
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { padding ->
        content(padding)
    }
}