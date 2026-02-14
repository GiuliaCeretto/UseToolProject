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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.data.dao.ToolEntity

/**
 * Versione aggiornata di ToolCardSmall che utilizza ToolEntity.
 * La logica dei prezzi e dei dati è ora legata ai campi del DB locale.
 */
@Composable
fun ToolCardSmall(
    tool: ToolEntity, // Utilizza ToolEntity
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(180.dp)
            .padding(6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // HEADER: NOME + PREZZO + ICONA
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    // Nome recuperato da ToolEntity
                    Text(
                        text = tool.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    // Logica prezzo basata sul campo 'type' (acquisto vs noleggio)
                    val priceLabel = if (tool.type == "noleggio") {
                        "€ ${tool.price}/h"
                    } else {
                        "€ ${tool.price}"
                    }

                    Text(
                        text = priceLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Placeholder Icon dato che ToolEntity non dispone di imageRes
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = tool.name,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // DESCRIZIONE (Sostituisce technicalData non presente nell'Entity)
            Column {
                Text(
                    text = "Descrizione",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )
            }
        }
    }
}