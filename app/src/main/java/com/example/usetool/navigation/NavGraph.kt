package com.example.usetool.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.usetool.screens.*
import com.example.usetool.screens.search.*
import com.example.usetool.screens.profile.*
import com.example.usetool.screens.linking.*
import com.example.usetool.screens.consulting.*
import com.example.usetool.screens.tool.*
import com.example.usetool.screens.distributor.*
import com.example.usetool.screens.cart.*
import com.example.usetool.ui.screens.expert.SchedaConsulenteScreen
import com.example.usetool.screens.payment.PagamentoScreen
import com.example.usetool.ui.screens.cart.CarrelloScreen
import com.example.usetool.ui.screens.home.HomeScreen
import com.example.usetool.ui.screens.linking.CollegamentoScreen
import com.example.usetool.ui.screens.locker.SchedaDistributoreScreen
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.ExpertViewModel
import com.example.usetool.ui.viewmodel.LinkingViewModel
import com.example.usetool.ui.viewmodel.SearchViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import com.example.usetool.ui.viewmodel.UserViewModel
import com.example.usetool.viewmodel.*
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
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier
    ) {

        // --- BOTTOM BAR ---
        composable(NavRoutes.Home.route) {
            HomeScreen(navController, useToolViewModel, cartViewModel)
        }

        composable(NavRoutes.Search.route) {
            SearchScreen(navController, useToolViewModel)
        }

        composable(NavRoutes.Collegamento.route) {
            CollegamentoScreen(
                navController = navController,
                useToolViewModel = useToolViewModel,
                cartViewModel = cartViewModel,
                linkingViewModel = linkingViewModel
            )
        }

        composable(NavRoutes.Consulenza.route) {
            val consultViewModel: ExpertViewModel = viewModel()
            Consulenza(navController, consultViewModel)
        }

        composable(
            route = NavRoutes.SchedaConsulente.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStack ->
            // Prendi l'id dall'argomento della route
            val id = backStack.arguments?.getString("id") ?: return@composable

            SchedaConsulenteScreen(
                navController = navController,
                expertId = id
            )
        }

        composable(NavRoutes.Profilo.route) {
            ProfiloScreen(navController, useToolViewModel)
        }

        composable(NavRoutes.Carrello.route) {
            CarrelloScreen(navController, cartViewModel)
        }

        composable(NavRoutes.Pagamento.route) {
            PagamentoScreen(navController, cartViewModel)
        }

        // --- DETTAGLI ---
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
    }
}

