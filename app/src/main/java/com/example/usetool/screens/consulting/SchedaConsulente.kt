package com.example.usetool.screens.consult

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.model.Expert
import com.example.usetool.viewmodel.ConsultViewModel
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaConsulenteScreen(
    navController: NavController,
    expertId: String,
    consultViewModel: ConsultViewModel = viewModel()
) {
    val expert: Expert? = consultViewModel.findExpertById(expertId)

    if (expert == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Esperto non trovato")
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nome e cognome
            Text(
                text = expert.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            // Foto dell’esperto
            expert.imageRes?.let { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = expert.name,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(vertical = 8.dp)
                )
            }

            // Profession / descrizione
            Text(
                text = expert.profession,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            // Descrizione
            Text(
                text = expert.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // Contatto
            Text(
                text = "Contatto",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            // Recapito telefonico (placeholder)
            Text(
                text = "+39 123 456 7890",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            // Pulsante contatta
            Button(
                onClick = { /* TODO: azione contatta */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.5f)
            ) {
                Text(text = "Contatta")
            }
        }
    }
}
