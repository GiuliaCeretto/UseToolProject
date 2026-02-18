package com.example.usetool.ui.screens

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.LinkingViewModel
import kotlin.math.PI
import kotlin.math.sin

// -------------------- CLASS DTMF PLAYER --------------------
class DTMFTonePlayer {

    private val sampleRate = 44100
    private val duration = 0.3

    private val dtmfMap = mapOf(
        '1' to Pair(697.0, 1209.0),
        '2' to Pair(697.0, 1336.0),
        '3' to Pair(697.0, 1477.0),
        '4' to Pair(770.0, 1209.0),
        '5' to Pair(770.0, 1336.0),
        '6' to Pair(770.0, 1477.0),
        '7' to Pair(852.0, 1209.0),
        '8' to Pair(852.0, 1336.0),
        '9' to Pair(852.0, 1477.0)
        // Se vuoi 0, *, # puoi aggiungerli qui
    )

    fun playTone(key: Char) {
        val frequencies = dtmfMap[key] ?: return
        val (f1, f2) = frequencies
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)

        for (i in samples.indices) {
            val angle1 = 2.0 * PI * i * f1 / sampleRate
            val angle2 = 2.0 * PI * i * f2 / sampleRate
            val value = (sin(angle1) + sin(angle2)) / 2
            samples[i] = (value * Short.MAX_VALUE).toInt().toShort()
        }

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            samples.size * 2,
            AudioTrack.MODE_STATIC
        )
        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
    }
}

// -------------------- SCREEN PRINCIPALE --------------------
@Composable
fun LinkingScreen(
    navController: NavController,
    viewModel: LinkingViewModel,
    lockerIdsFromCart: List<Int>,
    cartViewModel: CartViewModel
) {
    val inputCode by viewModel.inputCode.collectAsStateWithLifecycle()
    val isLinked by viewModel.isLinked.collectAsStateWithLifecycle()
    val connectedLockerName by viewModel.connectedLockerName.collectAsStateWithLifecycle()
    val selectedLockerLinkId by viewModel.selectedLockerLinkId.collectAsStateWithLifecycle()
    val availableLockers by viewModel.availableLockers.collectAsStateWithLifecycle()

    LaunchedEffect(lockerIdsFromCart) {
        viewModel.initLinking(lockerIdsFromCart)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLinked) {
            // --- SCENARIO C: SEI COLLEGATO ---
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "COLLEGAMENTO RIUSCITO",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Sei collegato al $connectedLockerName",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(Modifier.height(48.dp))

                if (lockerIdsFromCart.isNotEmpty()) {
                    Button(
                        onClick = {
                            selectedLockerLinkId?.let { id ->
                                // 🔹 Aggiorna il carrello prima di navigare
                                cartViewModel.refreshCart()

                                // 🔹 Naviga alla schermata di pagamento
                                navController.navigate(NavRoutes.Pagamento.createRoute(id))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Text("PROCEDI AL PAGAMENTO", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                }

                TextButton(onClick = { viewModel.resetSelection() }) {
                    Text("SCOLLEGATI / CAMBIA LOCKER", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

        } else if (selectedLockerLinkId == null) {
            // --- SCENARIO A: SELEZIONE LOCKER ---
            Text(
                "Seleziona un Locker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(availableLockers) { locker ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { viewModel.selectLocker(locker) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text("Locker #${locker.linkId} - ${locker.name}") },
                            supportingContent = { Text(locker.address) },
                            trailingContent = { Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
        } else {
            // --- SCENARIO B: TASTIERINO PIN ---
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp), tint = BluePrimary)
                    Text("Sblocco Locker", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Digita il PIN del Locker #$selectedLockerLinkId", color = Color.Gray)
                }


                /*
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier.size(20.dp).clip(CircleShape)
                                .background(if (inputCode.length > index) BluePrimary else Color.LightGray)
                        )
                    }
                }
                */


                // Tastierino con toni DTMF integrati
                TastierinoNumerico(viewModel)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = { viewModel.resetSelection() }) {
                        Text("Annulla", color = Color.Gray)
                    }
                }
            }
        }
    }
}

// -------------------- TASTIERINO NUMERICO --------------------
@Composable
fun TastierinoNumerico(viewModel: LinkingViewModel) {
    val buttons = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9')
    )
    val tonePlayer = remember { DTMFTonePlayer() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    KeyButton(key.toString()) {
                        viewModel.addDigit(key.digitToInt())
                        tonePlayer.playTone(key) // riproduce il tono DTMF
                    }
                }
            }
        }

        // Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.removeLastDigit() }) {
                Icon(Icons.Default.Backspace, contentDescription = "Backspace")
            }
        }
    }
}

// -------------------- PULSANTE SINGOLO --------------------
@Composable
fun KeyButton(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        color = Color(0xFFF5F5F5),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
