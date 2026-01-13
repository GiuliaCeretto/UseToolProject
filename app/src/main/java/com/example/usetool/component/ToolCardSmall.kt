package com.example.usetool.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.model.Tool

@Composable
fun ToolCardSmall(
    tool: Tool,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(180.dp)
            .padding(6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // HEADER: TESTI + IMMAGINE
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = tool.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp)) // leggermente più piccolo

                    Text(
                        text = when {
                            tool.pricePerHour != null ->
                                "€ ${tool.pricePerHour}/h"
                            tool.purchasePrice != null ->
                                "€ ${tool.purchasePrice}"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.width(12.dp))

                Image(
                    painter = painterResource(tool.imageRes),
                    contentDescription = tool.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // DATI TECNICI
            if (tool.technicalData.isNotEmpty()) {
                Spacer(Modifier.height(1.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tool.technicalData.entries
                        .take(3)
                        .forEachIndexed { index, entry ->

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = entry.key,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp) // più piccolo
                                )

                                Spacer(Modifier.height(2.dp))

                                Text(
                                    text = entry.value,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            if (index < minOf(2, tool.technicalData.size - 1)) {
                                VerticalDivider(
                                    modifier = Modifier
                                        .height(28.dp) // leggermente più corto
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                }
            } else {
                Spacer(Modifier.height(14.dp)) // mantiene altezza uniforme
            }
        }
    }
}

