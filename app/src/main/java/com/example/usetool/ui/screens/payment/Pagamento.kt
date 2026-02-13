package com.example.usetool.ui.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.ui.component.* // RISOLTO: Import corretto
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.CartViewModel

@Composable
fun PagamentoScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
) {
    // RISOLTO: Osserva la testata per il totale reale dal DB
    val cartHeader by cartViewModel.cartHeader.collectAsState()
    val total = cartHeader?.totaleProvvisorio ?: 0.0

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Totale da pagare", style = MaterialTheme.typography.titleMedium)
        Text("€${"%.2f".format(total)}", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // RISOLTO: Esegue la logica di checkout nel ViewModel
                cartViewModel.performCheckout()

                navController.navigate(NavRoutes.Home.route) {
                    popUpTo(NavRoutes.Home.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Conferma pagamento")
        }
    }
}