package com.example.usetool.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.usetool.screens.*
import com.example.usetool.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    useToolViewModel: UseToolViewModel,
    searchViewModel: SearchViewModel,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    collegamentoViewModel: CollegamentoViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.Home.route) {

        composable(NavRoutes.Home.route) {
            HomeScreen(navController, useToolViewModel, cartViewModel)
        }

        composable(NavRoutes.Search.route) {
            SearchScreen(navController, searchViewModel, cartViewModel)
        }

        composable(NavRoutes.Collegamento.route) {
            CollegamentoScreen(navController, collegamentoViewModel)
        }

        composable(NavRoutes.Consulenza.route) {
            ConsulenzaScreen(navController)
        }

        composable(NavRoutes.Profilo.route) {
            ProfiloScreen(navController, userViewModel)
        }

        composable(NavRoutes.Carrello.route) {
            CarrelloScreen(navController, cartViewModel)
        }

        composable(NavRoutes.Pagamento.route) {
            PagamentoScreen(navController, cartViewModel, collegamentoViewModel)
        }

        composable(
            NavRoutes.SchedaDistributore.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaDistributoreScreen(navController, id, useToolViewModel, cartViewModel)
        }

        composable(
            NavRoutes.SchedaStrumento.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaStrumentoScreen(navController, id, useToolViewModel, cartViewModel)
        }

        composable(
            NavRoutes.SchedaStrumentoFiltrata.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaStrumentoFiltrataScreen(navController, id, useToolViewModel, cartViewModel)
        }

        composable(
            NavRoutes.SchedaStrumentoNoleggiata.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaStrumentoNoleggiataScreen(navController, id, useToolViewModel, userViewModel)
        }
    }
}
