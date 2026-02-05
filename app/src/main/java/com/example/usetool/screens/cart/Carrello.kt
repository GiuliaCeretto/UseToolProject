package com.example.usetool.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrelloScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val items by cartViewModel.items.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (items.isEmpty()) {
            Text("Il carrello è vuoto")
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(items) { item ->
                Card {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.tool.name, style = MaterialTheme.typography.titleMedium)
                        Text("Durata: ${item.durationHours} ore")
                        Text("Prezzo: €${item.subtotal}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { cartViewModel.remove(item.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Rimuovi")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Totale: €${cartViewModel.total}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate(NavRoutes.Pagamento.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Procedi al pagamento")
        }
    }
}

