package com.example.usetool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.data.dao.ExpertEntity // IMPORTA IL DAO
import com.example.usetool.ui.theme.Green1
import com.example.usetool.ui.theme.Green2

@Composable
fun ConsultantCard(
    expert: ExpertEntity, // SOSTITUITO Expert (Model) con ExpertEntity (DAO)
    navController: NavController
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable {
                // Navigazione dinamica usando l'ID dell'esperto dal database
                navController.navigate("expert_detail/${expert.id}")
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // NOME E COGNOME (Uniti dal DAO)
            Text(
                text = "${expert.firstName} ${expert.lastName}",
                style = MaterialTheme.typography.titleMedium,
                color = Green1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // FOTO (Gestione placeholder se imageUrl è vuota)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Green2),
                contentAlignment = Alignment.Center
            ) {
                // Se hai immagini locali/risorse puoi usare expert.imageUrl
                // Qui usiamo un placeholder come nel tuo stile originale
                Image(
                    painter = painterResource(id = R.drawable.placeholder_profilo),
                    contentDescription = expert.firstName,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(10.dp))

            // PROFESSIONE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = expert.profession, // Dal DAO
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Green1
                    )
                )
            }
        }
    }
}