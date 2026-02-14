package com.example.usetool.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.theme.Green1
import com.example.usetool.ui.theme.Green2
import com.example.usetool.ui.viewmodel.ExpertViewModel

@Composable
fun SchedaConsulenteScreen(
    navController: NavController,
    expertId: String,
    expertViewModel: ExpertViewModel
) {
    val context = LocalContext.current

    // Osserva la lista degli esperti dal ViewModel
    val experts by expertViewModel.experts.collectAsState()

    // Utilizziamo remember per calcolare l'esperto solo quando la lista o l'id cambiano
    val expert = remember(experts, expertId) {
        experts.find { it.id == expertId }
    }

    var showDialog by remember { mutableStateOf(false) }

    // Utilizzo dello Scaffold per una struttura Material3 corretta
    Scaffold { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (expert == null) {
                // Stato di caricamento o esperto non trovato
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Green1)
                }
            } else {
                // UI Principale
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Foto Esperto
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(color = Green2, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_profilo),
                            contentDescription = "Foto di ${expert.firstName}",
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    }

                    Text(
                        text = "${expert.firstName} ${expert.lastName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = expert.profession,
                        color = Green1,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = expert.bio,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Contatto",
                        color = Green1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = expert.phoneNumber.ifEmpty { "Telefono non disponibile" },
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Pulsante Contatta
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Green1),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Contatta", color = Color.White)
                    }
                }

                // Dialog di conferma
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Conferma Chiamata", fontWeight = FontWeight.Bold) },
                        text = { Text("Vuoi chiamare ${expert.firstName} ${expert.lastName}?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${expert.phoneNumber}")
                                    }
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("CHIAMA ORA", color = Green1, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("ANNULLA", color = Color.Gray)
                            }
                        }
                    )
                }
            }
        }
    }
}