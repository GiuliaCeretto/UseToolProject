package com.example.usetool.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.usetool.R
import com.example.usetool.data.dao.ToolEntity

@Composable
fun ToolCardSmall(
    tool: ToolEntity,
    onClick: () -> Unit
) {
    // Lista dei dati tecnici filtrando solo valori non nulli e non vuoti
    val technicalData = listOf(
        "Autonomia" to (tool.autonomia?.takeIf { it.isNotBlank() } ?: "-"),
        "Potenza" to (tool.potenza?.takeIf { it.isNotBlank() } ?: "-"),
        "Peso" to (tool.peso?.takeIf { it.isNotBlank() } ?: "-")
    )

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(8.dp)) {
                // Immagine strumento
                AsyncImage(
                    model = tool.imageResName,
                    contentDescription = tool.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                    error = painterResource(id = R.drawable.placeholder_tool),
                    placeholder = painterResource(id = R.drawable.placeholder_tool)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tool.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Text(
                    text = tool.category.ifBlank { "-" },
                    color = Color.Gray,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Dati tecnici (solo se presenti)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Spazio tra le righe
                ) {
                    // Divider prima dei dati tecnici
                    Divider(
                        color = Color.Gray.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    technicalData.forEach { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.first,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                            )
                            Text(
                                text = entry.second,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Divider dopo i dati tecnici
                    Divider(
                        color = Color.Gray.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

            // Prezzo
                Text(
                    text = if (tool.type.lowercase() == "noleggio") "€${tool.price}/ora" else "€${tool.price}",
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            // Badge tipo strumento (noleggio/acquisto)
            val badgeColor = if (tool.type.lowercase() == "noleggio") Color(0xFFE3F2FD) else Color(0xFFFFF8E1)
            val textColor = if (tool.type.lowercase() == "noleggio") Color(0xFF1976D2) else Color(0xFFFFA000)

            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(bottomStart = 8.dp),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = tool.type.uppercase(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
