package com.example.usetool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.usetool.R
import com.example.usetool.data.dao.SlotEntity
import com.example.usetool.data.dao.ToolEntity

@Composable
fun DistributorToolRow(
    tool: ToolEntity,
    allSlots: List<SlotEntity>, // Lista degli slot passata dalla UI (osservata nel ViewModel)
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Verifica disponibilità reale basata sullo stato degli slot nel DB locale
    val isAvailable = allSlots.any { it.toolId == tool.id && it.status == "DISPONIBILE" }
    val alpha = if (isAvailable) 1f else 0.4f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .toggleable(
                    value = checked,
                    enabled = isAvailable,
                    onValueChange = onCheckedChange
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Utilizzo di un'immagine basata sul nome della risorsa o placeholder
            Image(
                painter = painterResource(id = R.drawable.placeholder_tool),
                contentDescription = tool.name,
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleSmall
                )

                // Visualizzazione descrizione tecnica dall'Entity
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )

                Spacer(Modifier.height(4.dp))

                // Prezzo formattato recuperato da ToolEntity
                Text(
                    text = "€${"%.2f".format(tool.price)}${if (tool.type == "noleggio") " / h" else ""}",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Checkbox abilitato solo se l'attrezzo è disponibile nello slot
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                enabled = isAvailable
            )
        }
    }
}