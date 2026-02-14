package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PagamentoScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
) {
    // Osserva il totale reale dal DB
    val cartHeader by cartViewModel.cartHeader.collectAsState()
    val total = cartHeader?.totaleProvvisorio ?: 0.0
    val snackbarHostState = remember { SnackbarHostState() }

    // Raccoglie errori di pagamento
    LaunchedEffect(cartViewModel.errorMessage) {
        cartViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Naviga alla Home se il carrello viene svuotato (checkout concluso)
    LaunchedEffect(cartHeader) {
        if (cartHeader == null) {
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Home.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Totale da pagare", style = MaterialTheme.typography.titleMedium)
            Text("€${"%.2f".format(total)}", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { cartViewModel.performCheckout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Conferma pagamento")
            }
        }
    }
}