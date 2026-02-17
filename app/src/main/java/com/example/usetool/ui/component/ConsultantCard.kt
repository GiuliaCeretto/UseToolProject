package com.example.usetool.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.data.dao.ExpertEntity
import com.example.usetool.navigation.NavRoutes
import coil.compose.AsyncImage
import com.example.usetool.ui.theme.Green1
import com.example.usetool.ui.theme.Green2

@Composable
fun ConsultantCard(
    expert: ExpertEntity,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate(NavRoutes.SchedaConsulente.createRoute(expert.id))
            },
        shape = RoundedCornerShape(8.dp), // Angoli meno arrotondati come nel design
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray) // Bordo sottile grigio
    ) {
        Column(
            modifier = Modifier.padding(20.dp), // Padding interno generoso
            horizontalAlignment = Alignment.Start // Tutto allineato a sinistra
        ) {
            // Icona "Torna indietro" (Opzionale, se fa parte della card)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp).padding(bottom = 12.dp),
                tint = Color.DarkGray
            )

            // Contenitore Immagine con sfondo verde chiaro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F9ED)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = expert.imageUrl, // URL dal database
                    contentDescription = "Immagine di ${expert.profession}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.placeholder_profilo),
                    placeholder = painterResource(id = R.drawable.placeholder_profilo)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Titolo Professione (es. Elettricista)
            Text(
                text = expert.profession,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A9078) // Il verde scuro del titolo
                )
            )

            Spacer(Modifier.height(8.dp))

            // Descrizione
            Text(
                text = "Conosco bene i vecchi impianti delle case in affitto. Scrivimi se devi sostituire una placchetta rotta...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            // Specialità
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A9078)
                    )
                    ) {
                        append("Specialità: ")
                    }
                    append("Spiegazioni semplici, zero termini tecnici")
                },
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(24.dp))

            // Pulsante Contatta
            Button(
                onClick = { /* Azione contatta */ },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Non occupa tutta la larghezza
                    .align(Alignment.CenterHorizontally)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B8F7B)), // Verde salvia
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("CONTATTA", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}