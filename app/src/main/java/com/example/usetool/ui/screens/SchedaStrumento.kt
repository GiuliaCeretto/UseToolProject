package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
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
    // 1. OSSERVAZIONE DATI: Recuperiamo gli strumenti e gli slot da Room
    val tools by viewModel.topTools.collectAsStateWithLifecycle()
    val allSlots by viewModel.slots.collectAsStateWithLifecycle()

    val tool = tools.find { it.id == id } ?: return

    // 2. LOGICA DI STATO: Calcoliamo la disponibilità e lo stato del carrello
    var isProcessing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val isAlreadyInCart = remember(allSlots) {
        allSlots.any { it.toolId == tool.id && it.status == "IN_CARRELLO" }
    }

    val availableSlot = remember(allSlots) {
        allSlots.find { it.toolId == tool.id && it.status == "DISPONIBILE" }
    }

    // Flag per la scrittura testuale di non disponibilità
    val isNotAvailable = availableSlot == null && !isAlreadyInCart

    LaunchedEffect(isAlreadyInCart) {
        if (isAlreadyInCart && isProcessing) {
            isProcessing = false
            showDialog = true
        }
    }

    val canClick = availableSlot != null && !isAlreadyInCart && !isProcessing
    val buttonText = when {
        isProcessing -> "AGGIUNTA IN CORSO..."
        isAlreadyInCart -> "GIÀ NEL CARRELLO"
        availableSlot == null -> "NON DISPONIBILE"
        else -> "PRENOTA ORA"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dettagli", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(1.4f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF6))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_tool),
                        contentDescription = tool.name,
                        modifier = Modifier.fillMaxSize(0.8f),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(text = tool.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = tool.category, color = Color.Gray, fontSize = 14.sp)

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SpecCard("Potenza", "18 V", Modifier.weight(1f))
                    SpecCard("Autonomia", "2-3 ore", Modifier.weight(1f))
                    SpecCard("Peso", "1.5 Kg", Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(24.dp))

                // Visualizzazione del prezzo e scrittura 'Non disponibile'
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A237E))) {
                                append("€${tool.price}")
                            }
                            if (tool.type == "noleggio") append("/ora")
                        },
                        style = MaterialTheme.typography.titleMedium
                    )

                    // 🔥 SCRITTURA 'NON DISPONIBILE'
                    if (isNotAvailable) {
                        Text(
                            text = "Non disponibile",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (canClick && availableSlot != null) {
                            isProcessing = true
                            cartVM.addToolToCart(tool, availableSlot)
                        }
                    },
                    enabled = canClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlreadyInCart) Color.Gray else Color(0xFF2E4077),
                        disabledContainerColor = if (isAlreadyInCart) Color.Gray else Color(0xFF2E4077).copy(alpha = 0.5f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = buttonText, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Carrello") },
            text = { Text("${tool.name} aggiunto con successo!") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK", color = Color(0xFF1A237E))
                }
            }
        )
    }
}

@Composable
fun SpecCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}