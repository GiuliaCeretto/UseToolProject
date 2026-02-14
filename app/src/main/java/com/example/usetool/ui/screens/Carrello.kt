package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.ui.component.CartItemCard
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.GreyLight
import com.example.usetool.ui.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CarrelloScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val cartHeader by cartViewModel.cartHeader.collectAsState()
    val items by cartViewModel.cartItems.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    // Raccoglie errori (es. checkout fallito o carrello vuoto)
    LaunchedEffect(cartViewModel.errorMessage) {
        cartViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val filteredItems = items.filter { it.toolName.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Il Tuo Carrello",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cerca negli articoli") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = GreyLight
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Il carrello è vuoto", color = MaterialTheme.colorScheme.secondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = { },
                            onDecrease = { },
                            onRemove = { cartViewModel.removeItem(item.slotId) }
                        )
                    }
                }
            }

            val totale = cartHeader?.totaleProvvisorio ?: 0.0

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Totale", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "€ ${"%.2f".format(totale)}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { cartViewModel.performCheckout() },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("Paga Ora")
                    }
                }
            }
        }
    }
}