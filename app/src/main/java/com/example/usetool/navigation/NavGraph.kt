package com.example.usetool.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.usetool.screens.*
import com.example.usetool.viewModel.UseToolViewModel

@Composable
fun NavGraph(viewModel: UseToolViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.Home.route) {

        composable(NavRoutes.Home.route) {
            Home(navController, viewModel)
        }

        composable(
            NavRoutes.Distributore.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("id") ?: ""
            Distributore(navController, viewModel, id)
        }

        composable(
            NavRoutes.Strumento.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("id") ?: ""
            Strumento(navController, viewModel, id)
        }

        composable(NavRoutes.Consulenza.route) {
            Consulenza(navController, viewModel)
        }

        composable(NavRoutes.MieiStrumenti.route) {
            MieiStrumenti(navController, viewModel)
        }

        composable(
            NavRoutes.InizioNoleggio.route,
            arguments = listOf(navArgument("toolId") { type = NavType.StringType })
        ) {
            val toolId = it.arguments?.getString("toolId") ?: ""
            InizioNoleggio(navController, viewModel, toolId)
        }

        composable(NavRoutes.Ricerca.route) {
            Ricerca(navController, viewModel)
        }

        composable(NavRoutes.Profilo.route) {
            Profilo(navController, viewModel)
        }

        composable(NavRoutes.Collegamento.route) {
            CollegamentoScreen(navController, viewModel)
        }

    }
}
