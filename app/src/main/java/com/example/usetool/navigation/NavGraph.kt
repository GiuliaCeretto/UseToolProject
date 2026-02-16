package com.example.usetool.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.usetool.ui.screens.*
import com.example.usetool.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    useToolViewModel: UseToolViewModel,
    searchViewModel: SearchViewModel,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    expertViewModel: ExpertViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route,
        modifier = modifier
    ) {

        composable(NavRoutes.Home.route) {
            HomeScreen(navController, useToolViewModel, userViewModel)
        }

        composable(NavRoutes.Search.route) {
            SearchScreen(navController, searchViewModel, useToolViewModel)
        }

        composable(NavRoutes.Collegamento.route) {
            CollegamentoScreen(navController)
        }

        composable(NavRoutes.Consulenza.route) {
            Consulenza(navController, expertViewModel)
        }

        composable(NavRoutes.Profilo.route) {
            ProfiloScreen(navController, userViewModel)
        }

        composable(NavRoutes.Carrello.route) {
            CarrelloScreen(navController, cartViewModel)
        }

        // --- SCHERMATE DI DETTAGLIO ---

        composable(
            route = NavRoutes.SchedaConsulente.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaConsulenteScreen(id, expertViewModel)
        }

        composable(
            route = NavRoutes.SchedaStrumento.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaStrumentoScreen(navController, id, useToolViewModel, cartViewModel)
        }

        composable(
            route = NavRoutes.SchedaDistributore.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaDistributoreScreen(navController, id, useToolViewModel, cartViewModel)
        }

        composable(NavRoutes.Pagamento.route) {
            PagamentoScreen(navController, cartViewModel, userViewModel = userViewModel)
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(navController, userViewModel)
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(navController, userViewModel) //
        }
    }
}