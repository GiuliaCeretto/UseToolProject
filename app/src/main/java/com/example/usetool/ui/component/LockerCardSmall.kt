package com.example.usetool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.YellowPrimary

@Composable
fun LockerCardSmall(
    locker: LockerEntity,
    address: String,
    distanceKm: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Immagine o Icona del Locker
            Image(
                painter = painterResource(R.drawable.placeholder_locker),
                contentDescription = null,
                modifier = Modifier.size(60.dp).align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            // Nome del Locker
            Text(
                text = locker.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                color = BluePrimary
            )

            // VIA (Indirizzo) - Aggiunta qui
            Text(
                text = address,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1
            )

            Spacer(Modifier.height(4.dp))

            // Distanza
            Text(
                text = "${"%.1f".format(distanceKm)} Km",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary
            )
        }
    }
}