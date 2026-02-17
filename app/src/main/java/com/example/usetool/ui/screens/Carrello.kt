package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.CartItemCard
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarrelloScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val cartHeader by cartViewModel.cartHeader.collectAsStateWithLifecycle()
    val items by cartViewModel.cartItems.collectAsStateWithLifecycle()
    val isProcessing by cartViewModel.isProcessing.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 🔥 Calcolo dei Locker ID unici presenti nel carrello
    val uniqueLockerIds = remember(items) {
        items.mapNotNull { it.lockerId }.distinct()
    }

    LaunchedEffect(Unit) {
        cartViewModel.refreshCart()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("IL TUO CARRELLO", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Riepilogo Articoli",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (items.isEmpty() && !isProcessing) {
                    // ... (Stessa logica carrello vuoto)
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
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
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(items, key = { it.slotId }) { item ->
                            Column {
                                val isNoleggio = item.toolId.contains("RENT", ignoreCase = true)

                                Surface(
                                    color = if (isNoleggio) Color(0xFFFFF3E0) else Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        text = if (isNoleggio) "NOLEGGIO" else "ACQUISTO",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNoleggio) Color(0xFFE65100) else BluePrimary
                                    )
                                }

                                CartItemCard(
                                    item = item,
                                    onIncrease = { cartViewModel.updateItemQuantity(item.slotId, item.quantity + 1) },
                                    onDecrease = { if (item.quantity > 1) cartViewModel.updateItemQuantity(item.slotId, item.quantity - 1) },
                                    onRemove = { cartViewModel.removeItem(item.slotId) }
                                )
                            }
                        }
                    }
                }

                // FOOTER
                val totale = cartHeader?.totaleProvvisorio ?: 0.0

                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(24.dp),
                    border = AssistChipDefaults.assistChipBorder(enabled = true)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // 🔥 Avviso informativo se ci sono più locker
                        if (uniqueLockerIds.size > 1) {
                            Text(
                                text = "Attenzione: gli oggetti sono in ${uniqueLockerIds.size} locker diversi.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

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
                                val idsString = uniqueLockerIds.joinToString(",")
                                navController.navigate(NavRoutes.Linking.createRoute(idsString))
                            },
                            enabled = items.isNotEmpty() && !isProcessing,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("COLLEGATI AL LOCKER", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}