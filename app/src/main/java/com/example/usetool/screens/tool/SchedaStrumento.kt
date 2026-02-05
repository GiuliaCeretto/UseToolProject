package com.example.usetool.screens.tool

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tool.technicalData.forEach { (label, value) ->
                    TechBox(label, value)
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
                    }
                ) { Text("Scheda tecnica") }

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        tool.videoUrl?.let {
                            // Apri video
                        } ?: println("Video non disponibile")
                    }
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
                                fontWeight = FontWeight.ExtraBold
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
                                fontWeight = FontWeight.ExtraBold
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
                    onClick = { cartVM.add(tool, null) },
                    modifier = Modifier.fillMaxWidth()
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
        }
    }
}

@Composable
private fun TechBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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


