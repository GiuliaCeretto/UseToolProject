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
    linkingViewModel: LinkingViewModel,
    orderViewModel: OrderViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.route,
        modifier = modifier
    ) {
        // --- HOME ---
        composable(NavRoutes.Home.route) {
            HomeScreen(navController, useToolViewModel, userViewModel)
        }

        // --- RICERCA ---
        composable(NavRoutes.Search.route) {
            SearchScreen(navController, searchViewModel, useToolViewModel)
        }

        // --- COLLEGAMENTO (LOCKER SELECTION) ---
        composable(NavRoutes.Collegamento.route) {
            // Il tuo file si chiama LinkingViewModel, ma la schermata nel file Linking.kt
            // probabilmente si chiama LinkingScreen (verifica se è LinkingScreen o CollegamentoScreen)
            LinkingScreen(
                navController = navController,
                viewModel = linkingViewModel,
                lockerIdsFromCart = emptyList(),
                cartViewModel = cartViewModel
            )
        }

        // --- LINKING (CON PIN) ---
        composable(
            route = NavRoutes.Linking.route,
            arguments = listOf(navArgument("lockerIds") { type = NavType.StringType })
        ) { backStack ->
            val idsString = backStack.arguments?.getString("lockerIds") ?: ""
            val lockerIds = idsString.split(",")
                .filter { it.isNotEmpty() }
                .mapNotNull { it.toIntOrNull() }

            LinkingScreen(
                navController = navController,
                viewModel = linkingViewModel,
                lockerIdsFromCart = lockerIds,
                cartViewModel = cartViewModel
            )
        }

        // --- CONSULENZA ---
        composable(NavRoutes.Consulenza.route) {
            // Nei tuoi file la funzione si chiama Consulenza
            Consulenza(navController, expertViewModel)
        }

        // --- PROFILO ---
        composable(NavRoutes.Profilo.route) {
            // Nel tuo progetto si chiama ProfiloScreen
            ProfiloScreen(navController, userViewModel)
        }

        // --- CARRELLO ---
        composable(NavRoutes.Carrello.route) {
            // Nel tuo progetto si chiama CarrelloScreen
            CarrelloScreen(navController, cartViewModel)
        }

        // --- SCHEDA CONSULENTE ---
        composable(
            route = NavRoutes.SchedaConsulente.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            // Nel tuo progetto si chiama SchedaConsulenteScreen
            SchedaConsulenteScreen(id, expertViewModel)
        }

        // --- SCHEDA STRUMENTO ---
        composable(
            route = NavRoutes.SchedaStrumento.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            // Nel tuo progetto si chiama SchedaStrumentoScreen
            SchedaStrumentoScreen(navController, id, useToolViewModel, cartViewModel)
        }

        // --- SCHEDA DISTRIBUTORE ---
        composable(
            route = NavRoutes.SchedaDistributore.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaDistributoreScreen(navController, id, useToolViewModel, cartViewModel)
        }

        // --- PAGAMENTO ---
        composable(
            route = NavRoutes.Pagamento.route,
            arguments = listOf(navArgument("lockerId") { type = NavType.IntType })
        ) { backStack ->
            val lockerId = backStack.arguments?.getInt("lockerId") ?: 0

            PagamentoScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                userViewModel = userViewModel,
                orderViewModel = orderViewModel,
                lockerId = lockerId
            )
        }

        // --- RITIRO ---
        composable(
            route = NavRoutes.Ritiro.route,
            arguments = listOf(navArgument("lockerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lockerId = backStackEntry.arguments?.getInt("lockerId") ?: 0

            RitiroScreen(
                navController = navController,
                userViewModel = userViewModel,
                orderViewModel = orderViewModel,
                lockerId = lockerId
            )
        }

        // --- AUTH ---
        composable(NavRoutes.Login.route) {
            LoginScreen(navController, userViewModel)
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(navController, userViewModel)
        }
    }
}