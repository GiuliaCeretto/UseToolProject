package com.example.usetool.ui.screens.expert

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Necessario per LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.ui.viewmodel.ExpertViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaConsulenteScreen(
    navController: NavController,
    expertId: String,
    expertViewModel: ExpertViewModel
) {
    // Recupera il contesto corretto per avviare l'Intent
    val context = LocalContext.current

    val experts by expertViewModel.experts.collectAsState()
    val expert = experts.find { it.id == expertId }

    if (expert == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Esperto non trovato")
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "${expert.firstName} ${expert.lastName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto di ${expert.firstName}",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = expert.profession,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = expert.bio,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Informazioni di Contatto", style = MaterialTheme.typography.titleSmall)

            // Visualizza il numero di telefono reale dell'entity
            Text(text = "Telefono: ${expert.phoneNumber}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "ID Esperto: ${expert.id}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Crea l'Intent per aprire il tastierino telefonico
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${expert.phoneNumber}")
                    }
                    // Avvia l'attività usando il contesto recuperato
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                // Testo del pulsante aggiornato per riflettere l'azione di chiamata
                Text("Chiama ${expert.firstName}")
            }
        }
    }
}