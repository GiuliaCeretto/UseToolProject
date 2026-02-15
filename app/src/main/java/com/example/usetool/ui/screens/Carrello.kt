package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.CartItemCard
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.GreyLight
import com.example.usetool.ui.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrelloScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    // Osservazione reattiva dei dati dal database locale (Room)
    val cartHeader by cartViewModel.cartHeader.collectAsStateWithLifecycle()
    val items by cartViewModel.cartItems.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    // Filtro locale degli articoli
    val filteredItems = remember(items, searchQuery) {
        items.filter { it.toolName.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CARRELLO", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "I tuoi articoli",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Barra di ricerca interna al carrello
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cerca nel carrello...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = GreyLight
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stato vuoto
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Il carrello è vuoto", style = MaterialTheme.typography.bodyLarge)
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Torna allo shop", color = BluePrimary)
                        }
                    }
                }
            } else {
                // Lista articoli con scorrimento
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredItems, key = { it.slotId }) { item ->
                        CartItemCard(
                            item = item,
                            onIncrease = { /* Implementare se necessario */ },
                            onDecrease = { /* Implementare se necessario */ },
                            onRemove = { cartViewModel.removeItem(item.slotId) }
                        )
                    }
                }
            }

            // --- FOOTER RIEPILOGO ---
            val totale = cartHeader?.totaleProvvisorio ?: 0.0

            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Totale provvisorio", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "€ ${"%.2f".format(totale)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = BluePrimary,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Navigazione alla PagamentoScreen passata come riferimento
                            navController.navigate(NavRoutes.Pagamento.route)
                        },
                        enabled = items.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text(
                            text = "VAI AL PAGAMENTO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}