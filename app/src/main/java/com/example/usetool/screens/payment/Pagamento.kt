package com.example.usetool.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.CartViewModel

@Composable
fun PagamentoScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
) {
    val total = cartViewModel.total

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {

        Text("Totale da pagare", style = MaterialTheme.typography.titleMedium)
        Text("€$total", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                cartViewModel.clear()
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
