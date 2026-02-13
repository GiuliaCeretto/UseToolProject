package com.example.usetool.screens.tool

import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.usetool.component.*
import com.example.usetool.ui.theme.*
import com.example.usetool.viewmodel.CartViewModel
import com.example.usetool.viewmodel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaStrumentoScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    val tool = viewModel.findToolById(id) ?: return
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // IMMAGINE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(tool.imageRes),
                contentDescription = tool.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {

            // NOME STRUMENTO
            Text(
                tool.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            // DESCRIZIONE BREVE
            Text(
                text = tool.shortDescription,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            // DATI TECNICI
            val techList = tool.technicalData.toList()
            val chunkedData = techList.chunked((techList.size + 2) / 3)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chunkedData.forEach { columnData ->
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        columnData.forEach { (label, value) ->
                            TechBox(
                                title = label,
                                value = value,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // PDF / VIDEO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        tool.pdfUrl?.let {
                            // Apri PDF
                        } ?: println("PDF non disponibile")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = LightGrayBackground,
                        contentColor = BluePrimary
                    )
                ) { Text("Scheda tecnica") }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        tool.videoUrl?.let {
                            // Apri video
                        } ?: println("Video non disponibile")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = LightGrayBackground,
                        contentColor = BluePrimary
                    )
                ) { Text("Video tutorial") }
            }

            Spacer(Modifier.height(20.dp))

            // DESCRIZIONE COMPLETA
            Text(
                text = tool.fullDescription,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            // PREZZO
            Text(
                buildAnnotatedString {
                    if (tool.pricePerHour != null) {
                        append("€")
                        withStyle(
                            SpanStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = BluePrimary
                            )
                        ) {
                            append("${tool.pricePerHour}")
                        }
                        append("/ora")
                    } else {
                        append("Prezzo acquisto: €")
                        withStyle(
                            SpanStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = BluePrimary
                            )
                        ) {
                            append("${tool.purchasePrice}")
                        }
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            // DISPONIBILITÀ
            if (tool.available) {
                Button(
                    onClick = {
                        cartVM.add(tool, null)
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
                    text = "Strumento non disponibile",
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
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text(
                                text = "OK",
                                color = BluePrimary // <- testo blu
                            )
                        }
                    }
                )
            }
        }
    }
}

// TECHBOX
@Composable
private fun TechBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
