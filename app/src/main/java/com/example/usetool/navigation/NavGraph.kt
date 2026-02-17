package com.example.usetool.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.usetool.data.service.toPurchaseEntity
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
    //purchaseViewModel: PurchaseViewModel,
    //rentalViewModel: RentalViewModel,
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
            LinkingScreen(
                navController = navController,
                viewModel = linkingViewModel,
                lockerIdsFromCart = emptyList()
            )
        }

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
                lockerIdsFromCart = lockerIds
            )
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

        composable(
            route = NavRoutes.Pagamento.route,
            arguments = listOf(navArgument("lockerId") { type = NavType.IntType })
        ) { backStack ->
            val lockerId = backStack.arguments?.getInt("lockerId") ?: 0

            PagamentoScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                userViewModel = userViewModel,
                lockerId = lockerId
            )
        }

        // ------------------ ROTTA RITIRO ------------------
        composable(NavRoutes.Ritiro.route) {
            val cartItems by cartViewModel.cartItems.collectAsStateWithLifecycle()

            val purchaseList = cartItems.map { it.toPurchaseEntity() }

            RitiroScreen(
                navController = navController,
                purchases = purchaseList,
                rentals = emptyList()
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(navController, userViewModel)
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(navController, userViewModel)
        }
    }
}
