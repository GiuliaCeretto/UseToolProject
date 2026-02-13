package com.example.usetool.screens.consulting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.usetool.model.Expert
import com.example.usetool.ui.theme.Green1
import com.example.usetool.ui.theme.Green2
import com.example.usetool.viewmodel.ConsultViewModel

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
        // Stato per il popup di conferma
        var showDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // FOTO DENTRO RETTANGOLO GREEN2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = Green2, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                expert.imageRes?.let { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = expert.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // NOME
            Text(
                text = expert.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )

            // PROFESSIONE
            Text(
                text = expert.profession,
                color = Green1,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                textAlign = TextAlign.Start
            )

            // DESCRIZIONE
            Text(
                text = expert.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CONTATTO
            Text(
                text = "Contatto",
                color = Green1,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )

            Text(
                text = "+39 123 456 7890",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                textAlign = TextAlign.Start
            )

            // PULSANTE CONTATTA CENTRATO
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Green1),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(
                        text = "Contatta",
                        color = Color.White
                    )
                }
            }

            // ALERT DIALOG POPUP
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Conferma") },
                    text = { Text("Esperto contattato") },
                    confirmButton = {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text(
                                text = "OK",
                                color = Green1
                            )
                        }
                    }
                )
            }
        }
    }
}
