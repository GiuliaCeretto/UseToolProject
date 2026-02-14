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

        // --- SCHERMATE PRINCIPALI (BOTTOM BAR) ---

        composable(NavRoutes.Home.route) {
            // CORRETTO: HomeScreen richiede UseToolViewModel e UserViewModel per i noleggi attivi
            HomeScreen(
                navController = navController,
                vm = useToolViewModel,
                userVm = userViewModel
            )
        }

        composable(NavRoutes.Search.route) {
            // CORRETTO: SearchScreen deve utilizzare il SearchViewModel dedicato
            SearchScreen(
                navController = navController,
                searchVm = searchViewModel,
                useToolVm = useToolViewModel
            )
        }

        composable(NavRoutes.Collegamento.route) {
            CollegamentoScreen(navController = navController)
        }

        composable(NavRoutes.Consulenza.route) {
            // CORRETTO: Consulenza utilizza l'ExpertViewModel per la lista esperti
            Consulenza(
                navController = navController,
                expertViewModel = expertViewModel
            )
        }

        composable(NavRoutes.Profilo.route) {
            // CORRETTO: ProfiloScreen utilizza UseToolViewModel (come definito nel file Profilo.kt)
            ProfiloScreen(
                navController = navController,
                viewModel = useToolViewModel
            )
        }

        composable(NavRoutes.Carrello.route) {
            CarrelloScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        // --- SCHERMATE DI DETTAGLIO E FLUSSI ---

        composable(
            route = NavRoutes.SchedaConsulente.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            // CORRETTO: Passato ExpertViewModel per recuperare i dettagli dell'esperto tramite ID
            SchedaConsulenteScreen(
                navController = navController,
                expertId = id,
                expertViewModel = expertViewModel
            )
        }

        composable(
            route = NavRoutes.SchedaStrumento.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaStrumentoScreen(
                navController = navController,
                id = id,
                viewModel = useToolViewModel,
                cartVM = cartViewModel
            )
        }

        composable(
            route = NavRoutes.SchedaDistributore.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("id") ?: ""
            SchedaDistributoreScreen(
                navController = navController,
                id = id,
                viewModel = useToolViewModel,
                cartVM = cartViewModel
            )
        }

        composable(NavRoutes.Pagamento.route) {
            PagamentoScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}