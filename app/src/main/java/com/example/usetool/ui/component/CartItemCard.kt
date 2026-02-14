package com.example.usetool.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.usetool.data.dao.CartItemEntity // Riferimento al DAO
import com.example.usetool.ui.theme.BlueLight
import com.example.usetool.ui.theme.BluePrimary

@Composable
fun CartItemCard(
    item: CartItemEntity, // Sostituito Model con DAO Entity
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.toolName, // Preso da CartItemEntity
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID Slot: ${item.slotId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // FOOTER: PREZZO + CONTROLLI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // PREZZO (Dato statico salvato nell'Entity)
                Text(
                    text = "€ ${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BluePrimary,
                    fontWeight = FontWeight.ExtraBold
                )

                // CONTROLLI QUANTITÀ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlueLight,
                    modifier = Modifier.width(110.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Meno")
                        }
                        Text(
                            text = "1", // Nota: CartItemEntity attuale non ha campo quantità
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Più")
                        }
                    }
                }

                IconButton(
                    onClick = onRemove,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Rimuovi")
                }
            }
        }
    }
}