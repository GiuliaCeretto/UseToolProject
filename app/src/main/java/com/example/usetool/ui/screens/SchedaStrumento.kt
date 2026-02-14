package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.usetool.R
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaStrumentoScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    // Recupera l'attrezzo dai dati osservati nel ViewModel
    val tools by viewModel.topTools.collectAsState()
    val tool = tools.find { it.id == id } ?: return

    // Osserva gli slot per determinare la disponibilità reale
    val allSlots by viewModel.slots.collectAsState()
    val availableSlot = allSlots.find { it.toolId == tool.id && it.status == "DISPONIBILE" }

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // IMMAGINE (Placeholder rimosso tool.imageRes non presente in ToolEntity, uso placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_tool),
                contentDescription = tool.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                tool.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            // PREZZO DINAMICO basato sul tipo (Entity usa 'price' e 'type')
            Text(
                buildAnnotatedString {
                    append(if (tool.type == "noleggio") "Tariffa: €" else "Prezzo acquisto: €")
                    withStyle(
                        SpanStyle(
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = BluePrimary
                        )
                    ) {
                        append("${tool.price}")
                    }
                    if (tool.type == "noleggio") append("/ora")
                },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            // DISPONIBILITÀ E AGGIUNTA AL CARRELLO
            // Verifichiamo se esiste uno slot disponibile per questo attrezzo
            if (availableSlot != null) {
                Button(
                    onClick = {
                        // Utilizziamo il metodo corretto del ViewModel
                        cartVM.addToolToCart(tool, availableSlot)
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Aggiungi al carrello")
                }
            } else {
                Text(
                    text = "Strumento non disponibile nei distributori",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Conferma") },
                    text = { Text("Aggiunto al carrello") },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK", color = BluePrimary)
                        }
                    }
                )
            }
        }
    }
}