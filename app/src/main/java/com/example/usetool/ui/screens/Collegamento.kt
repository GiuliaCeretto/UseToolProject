@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.component.*
import com.example.usetool.ui.viewmodel.*

@Composable
fun CollegamentoScreen(
    navController: NavController,
    useToolViewModel: UseToolViewModel,
    cartViewModel: CartViewModel,
    linkingViewModel: LinkingViewModel
) {

    val isLinked by linkingViewModel.isLinked.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val isProcessing by cartViewModel.isProcessing.collectAsState()

    val locker = useToolViewModel.lockers.collectAsState().value.firstOrNull()

    // Calcolo totale
    val total = cartItems.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isLinked) {

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = { linkingViewModel.link() },
                modifier = Modifier
                    .size(150.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(75.dp)
            ) {
                Text("Collega")
            }

        } else {

            locker?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(R.drawable.placeholder_locker),
                            contentDescription = it.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(it.name, style = MaterialTheme.typography.titleMedium)
                            Text("Codice: ${it.id}")
                            Text(it.address)
                        }
                    }
                }
            }

            // ==========================
            // 🛒 RIEPILOGO CARRELLO
            // ==========================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {

                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Riepilogo Ordine", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    cartItems.forEach { item ->

                        val subtotal = item.price * item.quantity

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = item.toolName,
                                modifier = Modifier.weight(1f)
                            )

                            // Pulsante -
                            IconButton(
                                onClick = {
                                    cartViewModel.updateItemQuantity(
                                        item.slotId,
                                        item.quantity - 1
                                    )
                                }
                            ) {
                                Text("-")
                            }

                            Text(item.quantity.toString())

                            // Pulsante +
                            IconButton(
                                onClick = {
                                    cartViewModel.updateItemQuantity(
                                        item.slotId,
                                        item.quantity + 1
                                    )
                                }
                            ) {
                                Text("+")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("€${String.format("%.2f", subtotal)}")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Totale: €${String.format("%.2f", total)}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            cartViewModel.performCheckout { rentalIds ->
                                // Navigazione post-checkout
                                navController.navigate("checkout_success")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = cartItems.isNotEmpty() && !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Acquista")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { linkingViewModel.unlink() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Scollegati")
                    }
                }
            }
        }
    }
}
