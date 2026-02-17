package com.example.usetool.ui.component

import androidx.compose.foundation.Image
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
import com.example.usetool.R
import com.example.usetool.data.dao.ToolEntity
import coil.compose.AsyncImage

@Composable
fun ToolCardSmall(
    tool: ToolEntity,
    onClick: () -> Unit
) {
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
                AsyncImage(
                    model = tool.imageUrl,
                    contentDescription = tool.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                    error = painterResource(id = R.drawable.placeholder_tool),
                    placeholder = painterResource(id = R.drawable.placeholder_tool)
                )

                Text(
                    text = tool.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Text(
                    text = tool.category,
                    color = Color.Gray,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Prezzo dinamico (senza bottone)
                Text(
                    text = if (tool.type == "noleggio") "€${tool.price}/ora" else "€${tool.price}",
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            // Badge Tipologia (Acquisto vs Noleggio)
            val badgeColor = if (tool.type == "noleggio") Color(0xFFE3F2FD) else Color(0xFFFFF8E1)
            val textColor = if (tool.type == "noleggio") Color(0xFF1976D2) else Color(0xFFFFA000)

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

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}