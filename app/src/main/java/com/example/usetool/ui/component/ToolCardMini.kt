package com.example.usetool.ui.component // Pacchetto aggiornato secondo il tuo refactoring

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.data.dao.ToolEntity
import kotlin.math.roundToInt

/**
 * Versione "Mini" della ToolCard collegata al database locale.
 * Ideale per caroselli orizzontali o griglie dense.
 */
@Composable
fun ToolCardMini(
    tool: ToolEntity, // Riferimento al DAO
    distanceKm: Float? = null,
    onClick: () -> Unit
) {
    // Dimensioni fisse originali
    val cardWidth = 140.dp // Aumentato leggermente da 60.dp per rendere il testo leggibile
    val cardHeight = 160.dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // NOME (Dal DAO)
            Text(
                text = tool.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // ICONA/IMMAGINE
            // Poiché ToolEntity non ha imageRes, usiamo un'icona o un box placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }

            // INFO PREZZO E DISTANZA
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Prezzo basato sulla logica ToolEntity (prezzo unico vs orario)
                val priceLabel = if (tool.type == "noleggio") "€${tool.price}/h" else "€${tool.price}"

                Text(
                    text = priceLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                distanceKm?.let {
                    Text(
                        text = "${it.roundToInt()} km",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
        }
    }
}