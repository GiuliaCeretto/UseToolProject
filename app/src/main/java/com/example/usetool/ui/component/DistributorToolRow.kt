package com.example.usetool.ui.component // Pacchetto aggiornato al nuovo refactoring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.ui.theme.BlueLight
import com.example.usetool.ui.theme.BlueMedium
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.GreyMedium

/**
 * Versione aggiornata di DistributorToolRow che utilizza ToolEntity dal database.
 * Mantiene la stessa struttura visuale del componente originale.
 */
@Composable
fun DistributorToolRow(
    tool: ToolEntity, // Collegato al DAO
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Nota: ToolEntity non ha il campo 'available', assumiamo sia gestito a monte
    // o che l'attrezzo sia disponibile se presente nel database dei distributori.
    val alpha = 1f
    val priceColor = if (checked) BluePrimary else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GreyMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // COLONNA 1: IMMAGINE (Placeholder Icon per ToolEntity)
            Box(
                modifier = Modifier.weight(0.8f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = tool.name,
                    modifier = Modifier.size(48.dp),
                    tint = BluePrimary.copy(alpha = 0.6f)
                )
            }

            // COLONNA 2: NOME + DESCRIZIONE (Sostituisce TechnicalData)
            Column(
                modifier = Modifier.weight(2.4f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                // RIQUADRO INFORMAZIONI (Usa la descrizione dell'Entity)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BlueLight, RoundedCornerShape(8.dp))
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // COLONNA 3: CHECKBOX + PREZZO
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = BlueMedium,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Logica prezzo basata sul campo 'type' di ToolEntity
                val priceDisplay = if (tool.type == "noleggio") {
                    "€${tool.price} / h"
                } else {
                    "€${tool.price}"
                }

                Text(
                    text = priceDisplay,
                    color = priceColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp
                )
            }
        }
    }
}