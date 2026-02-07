package com.example.usetool.screens.linking

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
import com.example.usetool.component.*
import com.example.usetool.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegamentoScreen(
    navController: NavController,
    useToolViewModel: UseToolViewModel,
    cartViewModel: CartViewModel,
    linkingViewModel: LinkingViewModel
) {
    val isLinked by linkingViewModel.isLinked.collectAsState()
    val cartItems by cartViewModel.items.collectAsState()

    val locker = useToolViewModel.lockers.collectAsState().value.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isLinked) {
            // STATO NON COLLEGATO
            Spacer(modifier = Modifier.height(100.dp))
            Button(
                onClick = { linkingViewModel.link() },
                modifier = Modifier
                    .size(150.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(75.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Collega",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            // STATO COLLEGATO
            locker?.let {
                // Riquadro Locker
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
                        // Immagine distributore (placeholder)
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
                            Text("Codice: ${it.id}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(it.address, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Riepilogo carrello
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.tool.name, modifier = Modifier.weight(1f))
                            if (item.tool.purchasePrice != null) {
                                // Se acquistabile
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Qta:")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextField(
                                        value = item.quantity.toString(),
                                        onValueChange = { value ->
                                            value.toIntOrNull()?.let {
                                                cartViewModel.setQuantity(item.id, it)
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier.width(60.dp)
                                    )
                                }
                            } else if (item.tool.pricePerHour != null) {
                                // Se affittabile
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Ore:")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    TextField(
                                        value = item.durationHours.toString(),
                                        onValueChange = { value ->
                                            value.toIntOrNull()?.let {
                                                cartViewModel.setDuration(item.id, it)
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier.width(60.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "€${String.format("%.2f", item.subtotal)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        "Totale: €${String.format("%.2f", cartViewModel.total)}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* Logica acquisto */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Acquista")
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
