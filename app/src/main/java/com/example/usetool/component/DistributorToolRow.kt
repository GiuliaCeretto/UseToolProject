package com.example.usetool.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.model.Tool
import com.example.usetool.ui.theme.BlueLight
import com.example.usetool.ui.theme.BlueMedium
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.GreyMedium

@Composable
fun DistributorToolRow(
    tool: Tool,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val alpha = if (tool.available) 1f else 0.4f
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
                    enabled = tool.available,
                    onValueChange = onCheckedChange
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // COLONNA 1: IMMAGINE
            Box(
                modifier = Modifier.weight(0.8f),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(tool.imageRes),
                    contentDescription = tool.name,
                    modifier = Modifier
                        .size(64.dp)
                )
            }

            // COLONNA 2: NOME + DATI TECNICI
            Column(
                modifier = Modifier.weight(2.4f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // NOME CENTRATO
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                // RIQUADRO DATI TECNICI
                if (tool.technicalData.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BlueLight, RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
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
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp
                                            )
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = entry.value,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }

                                    if (index < minOf(2, tool.technicalData.size - 1)) {
                                        Spacer(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(28.dp)
                                                .background(Color.Gray.copy(alpha = 0.5f))
                                        )
                                    }
                                }
                        }
                    }
                } else {
                    Spacer(Modifier.height(14.dp))
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
                    enabled = tool.available,
                    colors = CheckboxDefaults.colors(
                        checkedColor = BlueMedium,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = tool.pricePerHour?.let { "€$it / h" }
                        ?: tool.purchasePrice?.let { "€$it" }
                        ?: "",
                    color = priceColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
