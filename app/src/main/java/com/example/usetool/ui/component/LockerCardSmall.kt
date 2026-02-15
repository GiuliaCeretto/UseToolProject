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

@Composable
fun LockerCardSmall(
    locker: LockerEntity,
    distanceKm: Double?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(110.dp) // Altezza leggermente aumentata per contenere meglio i testi
            .padding(6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Sfondo bianco pulito
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Elevazione aumentata per effetto "pop-up"
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Immagine del distributore (Locker)
            Image(
                painter = painterResource(R.drawable.placeholder_locker),
                contentDescription = locker.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Nome del distributore in grassetto
                Text(
                    text = locker.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // ID Locker stilizzato come sottotitolo
                Text(
                    text = "ID ${locker.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Distanza con icona Pin personalizzata
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.pin),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (distanceKm != null) "%.1f km".format(distanceKm) else "-- km",
                        style = MaterialTheme.typography.labelMedium,
                        color = BluePrimary, // Colore primario del tema
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}