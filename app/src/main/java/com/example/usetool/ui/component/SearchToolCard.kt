// SearchToolCard.kt
package com.example.usetool.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.ui.theme.*
import com.example.usetool.R
import coil.compose.AsyncImage

@Composable
fun SearchToolCard(
    tool: ToolEntity,
    calculatedDistance: Double?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Box {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(top = 16.dp)
            ) {

                Text(
                    tool.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = tool.imageUrl, // URL internet
                        contentDescription = tool.name,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.placeholder_tool),
                        placeholder = painterResource(id = R.drawable.placeholder_tool)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val distText = if (calculatedDistance != null)
                        "${String.format("%.1f", calculatedDistance)} Km"
                    else "-- Km"

                    // 👇 Prezzo dinamico
                    val priceText = if (tool.type == "noleggio")
                        "€${tool.price}/ora"
                    else
                        "€${tool.price}"

                    Text(
                        text = "$distText - $priceText",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1A237E),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            // 👇 Badge Noleggio / Acquisto
            val badgeColor = if (tool.type == "noleggio")
                Color(0xFFE3F2FD)
            else
                Color(0xFFFFF8E1)

            val textColor = if (tool.type == "noleggio")
                Color(0xFF1976D2)
            else
                Color(0xFFFFA000)

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
