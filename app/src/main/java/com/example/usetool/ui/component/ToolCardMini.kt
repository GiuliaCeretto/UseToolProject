package com.example.usetool.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.data.dao.ToolEntity // Import aggiornato per l'Entity
import kotlin.math.roundToInt

@Composable
fun ToolCardMini(
    tool: ToolEntity, // Utilizza ToolEntity invece del vecchio model
    distanceKm: Double? = null, // Cambiato in Double per coerenza con gli altri componenti
    onClick: () -> Unit
) {
    // Dimensioni fisse per tutte le card (come da codice originale)
    val cardWidth = 100.dp // Aumentato leggermente da 60.dp per leggibilità dei testi
    val cardHeight = 150.dp
    val imageHeight = 80.dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // NOME (Recuperato da ToolEntity)
            Text(
                text = tool.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 2,
                lineHeight = 14.sp
            )

            // FOTO (Placeholder dato che ToolEntity non ha imageRes)
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = tool.name,
                modifier = Modifier
                    .height(imageHeight)
                    .fillMaxWidth(),
                tint = MaterialTheme.colorScheme.primary
            )

            // DISTANZA E PREZZO
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                distanceKm?.let {
                    Text(
                        text = "${it.roundToInt()} km",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Logica Prezzo basata su 'type' dell'Entity
                val priceLabel = if (tool.type == "noleggio") "€${tool.price}/h" else "€${tool.price}"

                Text(
                    text = priceLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    maxLines = 1
                )
            }
        }
    }
}