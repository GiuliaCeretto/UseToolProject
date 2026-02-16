package com.example.usetool.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.usetool.data.dao.CartItemEntity
import com.example.usetool.ui.theme.BlueLight
import com.example.usetool.ui.theme.BluePrimary

@Composable
fun CartItemCard(
    item: CartItemEntity,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    // Determiniamo se è un noleggio per disabilitare i controlli quantità se necessario
    val isRental = item.toolId.contains("RENT", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // --- HEADER: NOME E DETTAGLI ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.toolName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )
                    Text(
                        text = "ID Slot: ${item.slotId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // --- FOOTER: PREZZO TOTALE + CONTROLLI ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calcolo prezzo totale per la riga (Prezzo unitario * Quantità)
                val totalLinePrice = item.price * item.quantity

                Column {
                    Text(
                        text = "€ ${"%.2f".format(totalLinePrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = BluePrimary,
                        fontWeight = FontWeight.Black
                    )
                    if (item.quantity > 1) {
                        Text(
                            text = "unitario: € ${"%.2f".format(item.price)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // CONTROLLI QUANTITÀ (Nascosti o disabilitati se è un noleggio)
                if (!isRental) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BlueLight,
                        modifier = Modifier.width(120.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = onDecrease,
                                modifier = Modifier.size(32.dp)
                            ) {
                                // Usiamo Remove (il meno) invece di Clear (la X)
                                Icon(Icons.Default.Remove, contentDescription = "Diminuisci", modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = item.quantity.toString(), // 🔥 Ora legge la quantità reale dal DAO
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                color = BluePrimary
                            )

                            IconButton(
                                onClick = onIncrease,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Aumenta", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // TASTO RIMOZIONE
                IconButton(
                    onClick = onRemove,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Rimuovi", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}