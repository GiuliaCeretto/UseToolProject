package com.example.usetool.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.model.Tool
import kotlin.math.roundToInt

@Composable
fun ToolCardMini(
    tool: Tool,
    distanceKm: Float? = null,
    onClick: () -> Unit
) {
    // Dimensioni fisse per tutte le card
    val cardWidth = 60.dp
    val cardHeight = 150.dp
    val imageHeight = 80.dp

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // NOME
            Text(
                text = tool.name ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                maxLines = 2
            )

            // FOTO
            Image(
                painter = painterResource(tool.imageRes),
                contentDescription = tool.name,
                modifier = Modifier
                    .height(imageHeight)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            // DISTANZA E PREZZO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                distanceKm?.let {
                    Text(
                        text = "${it.roundToInt()} km",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp)
                    )
                }

                Spacer(modifier = Modifier.width(26.dp))

                Text(
                    text = when {
                        tool.pricePerHour != null -> "€ ${tool.pricePerHour}/h"
                        tool.purchasePrice != null -> "€ ${tool.purchasePrice}"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp)
                )
            }
        }
    }
}

