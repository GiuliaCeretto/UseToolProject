package com.example.usetool.ui.screens

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaStrumentoScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    val context = LocalContext.current
    val tools by viewModel.topTools.collectAsStateWithLifecycle()
    val allSlots by viewModel.slots.collectAsStateWithLifecycle()
    val lockers by viewModel.lockers.collectAsStateWithLifecycle()

    val tool = tools.find { it.id == id } ?: return

    // 1. Rilevamento Tipo: Più robusto (trim e ignoreCase)
    val isRental = tool.type.trim().contains("rent", ignoreCase = true) ||
            tool.type.trim().contains("noleggio", ignoreCase = true)

    // 2. Verifica Carrello
    val isAlreadyInCart = remember(allSlots) {
        allSlots.any { it.toolId == tool.id && it.status == "IN_CARRELLO" }
    }

    // 3. Slot Disponibile
    val availableSlot = remember(allSlots) {
        allSlots.find { it.toolId == tool.id && it.status == "DISPONIBILE" && it.quantity > 0 }
    }

    val isEffectivelyAvailable = availableSlot != null
    val maxQuantity = availableSlot?.quantity ?: 0

    val toolSlot = allSlots.find { it.toolId == tool.id }
    val locker = lockers.find { it.id == toolSlot?.lockerId }
    val isFavorite = toolSlot?.isFavorite ?: false

    var isProcessing by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showPdfConfirmDialog by remember { mutableStateOf(false) }

    // Reset della quantità se è noleggio
    var selectedQuantity by remember { mutableIntStateOf(1) }

    LaunchedEffect(isAlreadyInCart) {
        if (isAlreadyInCart && isProcessing) {
            isProcessing = false
            showSuccessDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dettagli", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BluePrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = BluePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        toolSlot?.let { viewModel.toggleFavorite(it.id, !isFavorite) }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Default.StarBorder,
                            contentDescription = "Preferiti",
                            tint = if (isFavorite) YellowPrimary else GreyMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            val buttonText = when {
                isAlreadyInCart -> "GIÀ NEL CARRELLO"
                isProcessing -> "AGGIUNTA IN CORSO..."
                !isEffectivelyAvailable -> "NON DISPONIBILE"
                else -> "PRENOTA ORA"
            }

            val canClick = isEffectivelyAvailable && !isAlreadyInCart && !isProcessing

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 16.dp, clip = false)
                    .navigationBarsPadding(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)) { append("€${tool.price.toInt()}") }
                                    withStyle(SpanStyle(fontSize = 16.sp, color = GreyMedium)) { append(if (isRental) "/ora" else "") }
                                }
                            )
                            // Mostra il numero di pezzi disponibili SOLO per l'acquisto
                            if (!isRental && isEffectivelyAvailable) {
                                Text("Disponibili: $maxQuantity", fontSize = 12.sp, color = GreyMedium)
                            }
                        }

                        // 🔥 CORREZIONE: Il selettore appare SOLO se NON è noleggio E se disponibile
                        if (!isRental && isEffectivelyAvailable && !isAlreadyInCart) {
                            QuantitySelector(
                                quantity = selectedQuantity,
                                maxQuantity = maxQuantity,
                                onQuantityChange = { selectedQuantity = it }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (canClick && availableSlot != null) {
                                isProcessing = true
                                // Se è noleggio forziamo sempre 1, altrimenti prendiamo il valore del counter
                                val qty = if (isRental) 1 else selectedQuantity
                                cartVM.addMultipleToolsToCart(List(qty) { tool to availableSlot })
                            }
                        },
                        enabled = canClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BluePrimary,
                            contentColor = Color.White,
                            disabledContainerColor = GreyLight,
                            disabledContentColor = Color.White
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(text = buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrayBackground)
                .padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding() + 24.dp)
        ) {
            // Sezione Immagine
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.4f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GreyLight.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_tool),
                                contentDescription = tool.name,
                                modifier = Modifier.fillMaxSize(0.7f),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // Sezione Info
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tool.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = BluePrimary)
                            Text("Informazioni principali e specifiche tecniche", color = GreyMedium, fontSize = 14.sp)
                        }

                        val statusText = when {
                            isAlreadyInCart -> "IN CARRELLO"
                            isEffectivelyAvailable -> "DISPONIBILE"
                            else -> "NON DISPONIBILE"
                        }
                        val statusColor = when (statusText) {
                            "DISPONIBILE" -> Color(0xFF4CAF50)
                            "IN CARRELLO" -> YellowPrimary
                            else -> Color.Red
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, statusColor)
                        ) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SpecCard("Potenza", "18 V", Modifier.weight(1f))
                        SpecCard("Autonomia", "2-3 ore", Modifier.weight(1f))
                        SpecCard("Peso", "1.5 Kg", Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BluePrimary)) { append("Categoria: ") }
                        append(tool.category)
                    })
                    Text(text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = BluePrimary)) { append("Locker: ") }
                        append(locker?.name ?: "N/D")
                    })

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreyMedium,
                        lineHeight = 20.sp
                    )
                }
            }

            // Sezione Tutorial
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    HorizontalDivider(color = GreyLight.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 16.dp))
                    Text("Guida all'utilizzo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = BluePrimary)
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (!isVideoPlaying) {
                                Image(
                                    painter = painterResource(id = R.drawable.placeholder_tool),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().alpha(0.4f),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(onClick = {
                                    isVideoPlaying = true
                                    context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.youtube.com/watch?v=EVgd4gvY0hU".toUri()))
                                }) {
                                    Icon(Icons.Default.PlayCircleFilled, null, tint = YellowPrimary, modifier = Modifier.size(64.dp))
                                }
                            } else {
                                CircularProgressIndicator(color = YellowPrimary)
                                LaunchedEffect(Unit) { delay(2000); isVideoPlaying = false }
                            }
                        }
                    }
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Aggiunto!", color = BluePrimary, fontWeight = FontWeight.Bold) },
                text = { Text("${tool.name} è stato aggiunto con successo al carrello.") },
                confirmButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("OK", color = BluePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun QuantitySelector(quantity: Int, maxQuantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(LightGrayBackground, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
            Text("-", fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 18.sp)
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 12.dp),
            fontWeight = FontWeight.Bold,
            color = BluePrimary
        )
        IconButton(onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) }) {
            Text("+", fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 18.sp)
        }
    }
}

@Composable
fun SpecCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 12.sp, color = GreyMedium)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
        }
    }
}