package com.example.usetool.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.LinkingViewModel

@Composable
fun LinkingScreen(
    navController: NavController,
    viewModel: LinkingViewModel,
    lockerIdsFromCart: List<Int>
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

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier.size(20.dp).clip(CircleShape)
                                .background(if (inputCode.length > index) BluePrimary else Color.LightGray)
                        )
                    }
                }

                // Tastierino (Omettiamo per brevità, resta uguale al tuo precedente)
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

@Composable
fun TastierinoNumerico(viewModel: LinkingViewModel) {
    val buttons = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), listOf(-1, 0, -2))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { digit ->
                    when {
                        digit >= 0 -> KeyButton(digit.toString()) { viewModel.addDigit(digit) }
                        digit == -2 -> IconButton(onClick = { viewModel.removeLastDigit() }) { Icon(Icons.Default.Backspace, null) }
                        else -> Spacer(modifier = Modifier.size(72.dp))
                    }
                }
            }
        }
    }
}

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