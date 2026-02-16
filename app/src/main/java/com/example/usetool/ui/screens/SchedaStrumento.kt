package com.example.usetool.ui.screens

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel

const val PDF_MANUAL_URL = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"

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

    val isRental = tool.type.trim().contains("rent", ignoreCase = true) ||
            tool.type.trim().contains("noleggio", ignoreCase = true)

    val isAlreadyInCart = remember(allSlots) {
        allSlots.any { it.toolId == tool.id && it.status == "IN_CARRELLO" }
    }

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
                }
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
                modifier = Modifier.fillMaxWidth().shadow(16.dp).navigationBarsPadding(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(buildAnnotatedString {
                                withStyle(SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)) { append("€${tool.price.toInt()}") }
                                withStyle(SpanStyle(fontSize = 16.sp, color = GreyMedium)) { append(if (isRental) "/ora" else "") }
                            })
                            if (!isRental && isEffectivelyAvailable) {
                                Text("Disponibili: $maxQuantity", fontSize = 12.sp, color = GreyMedium)
                            }
                        }
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
                                val qty = if (isRental) 1 else selectedQuantity
                                cartVM.addMultipleToolsToCart(List(qty) { tool to availableSlot })
                            }
                        },
                        enabled = canClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
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
            modifier = Modifier.fillMaxSize().background(LightGrayBackground).padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding() + 24.dp)
        ) {
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.4f),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GreyLight.copy(alpha = 0.5f))
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

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(tool.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = BluePrimary)
                    Text("Informazioni principali e specifiche tecniche", color = GreyMedium, fontSize = 14.sp)

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
                    Text(text = tool.description, style = MaterialTheme.typography.bodyMedium, color = GreyMedium)
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                    HorizontalDivider(color = GreyLight.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 16.dp))
                    Text("Guida all'utilizzo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BluePrimary)
                    Spacer(Modifier.height(16.dp))

                    // Player Video
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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=EVgd4gvY0hU"))
                                    context.startActivity(intent)
                                }) {
                                    Icon(Icons.Default.PlayCircleFilled, null, tint = YellowPrimary, modifier = Modifier.size(64.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Sezione Download PDF
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GreyLight.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PictureAsPdf, null, tint = Color(0xFFE53935), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Manuale d'uso PDF", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = BluePrimary)
                                Text("Scarica per consultazione offline", fontSize = 12.sp, color = Color.Gray)
                            }
                            FilledIconButton(
                                onClick = {
                                    downloadPdf(context, PDF_MANUAL_URL)
                                    Toast.makeText(context, "Download avviato...", Toast.LENGTH_SHORT).show()
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = BluePrimary.copy(alpha = 0.1f), contentColor = BluePrimary)
                            ) {
                                Icon(Icons.Default.Download, null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(80.dp))
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Aggiunto!", fontWeight = FontWeight.Bold) },
                text = { Text("${tool.name} è nel carrello.") },
                confirmButton = { TextButton(onClick = { showSuccessDialog = false }) { Text("OK") } }
            )
        }
    }
}

@Composable
fun QuantitySelector(quantity: Int, maxQuantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(LightGrayBackground, RoundedCornerShape(12.dp)).padding(4.dp)
    ) {
        IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) { Text("-", fontWeight = FontWeight.Bold) }
        Text(text = quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
        IconButton(onClick = { if (quantity < maxQuantity) onQuantityChange(quantity + 1) }) { Text("+", fontWeight = FontWeight.Bold) }
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

fun downloadPdf(context: Context, url: String) {
    try {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Manuale Uso Strumento")
            .setDescription("Download in corso...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Manuale_Usetool.pdf")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        manager.enqueue(request)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
