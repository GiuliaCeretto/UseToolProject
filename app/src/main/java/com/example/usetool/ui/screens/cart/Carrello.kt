package com.example.usetool.ui.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.ui.viewmodel.CartViewModel

@Composable
fun CarrelloScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    // Osserva le Entity (CartEntity e CartItemEntity)
    val cartHeader by cartViewModel.cartHeader.collectAsState()
    val items by cartViewModel.cartItems.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Il Tuo Carrello",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (items.isEmpty()) {
            Box(modifier = Modifier.weight(1f)) {
                Text("Il carrello è vuoto")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.toolName, style = MaterialTheme.typography.titleMedium)
                                Text("Prezzo: €${item.price}")
                            }
                            Button(
                                onClick = { cartViewModel.removeItem(item.slotId) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Rimuovi")
                            }
                        }
                    }
                }
            }
        }

        // Totale calcolato e fornito dal database locale tramite il Repository
        val totale = cartHeader?.totaleProvvisorio ?: 0.0

        Text("Totale: €${"%.2f".format(totale)}", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = { cartViewModel.performCheckout() },
            modifier = Modifier.fillMaxWidth(),
            enabled = items.isNotEmpty()
        ) {
            Text("Procedi al Checkout")
        }
    }
}