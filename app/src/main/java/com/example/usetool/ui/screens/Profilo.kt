package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfiloScreen(
    navController: NavController,
    userVm: UserViewModel
) {
    // Osservazione dei dati reali dal ViewModel
    val profile by userVm.userProfile.collectAsStateWithLifecycle()
    val rentals by userVm.rentals.collectAsStateWithLifecycle()
    val purchases by userVm.purchases.collectAsStateWithLifecycle()

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.ITALY) }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER PROFILO ---
            item {
                Text(
                    text = "Profilo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFE0E0E0), CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        modifier = Modifier.size(100.dp),
                        tint = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Componente Info Utente
                UserMainInfoCard(
                    name = "${profile?.nome ?: "Mario"} ${profile?.cognome ?: "Rossi"}",
                    uid = profile?.uid?.take(5) ?: "32560",
                    email = profile?.email ?: "mario.rossi@gmail.com",
                    totalRentals = rentals.size,
                    activeRentals = rentals.count { it.statoNoleggio == "ATTIVO" }
                )
            }

            // --- BOTTONI AZIONE ---
            item {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(text = "Modifica", icon = Icons.Default.Settings, modifier = Modifier.weight(1f))
                    ActionButton(text = "Italiano 🇮🇹", modifier = Modifier.weight(1f))
                    ActionButton(
                        text = "Esci",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFFF3E0),
                        onClick = { userVm.performFullLogout() }
                    )
                }
            }

            // --- SEZIONE NOLEGGIO IN CORSO ---
            item {
                val activeRentals = rentals.filter { it.statoNoleggio == "ATTIVO" }
                SectionBox(
                    text = if (activeRentals.isNotEmpty()) "Hai ${activeRentals.size} noleggio/i in corso" else "Nessun noleggio in corso",
                    isButtonActive = activeRentals.isNotEmpty()
                )
            }

            // --- STORICO NOLEGGI ---
            val historicalRentals = rentals.filter { it.statoNoleggio != "ATTIVO" }
            if (historicalRentals.isNotEmpty()) {
                item {
                    Text(
                        "Storico Noleggi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp)
                    )
                }

                items(historicalRentals) { rental ->
                    HistoricalItemCard(
                        slotCode = "Slot ${rental.slotId.takeLast(3)}",
                        date = dateFormatter.format(Date(rental.dataInizio)),
                        cost = "€${"%.2f".format(rental.costoTotale)}",
                        tools = listOf(rental.toolName),
                        isRental = true
                    )
                }
            }

            // --- STORICO ACQUISTI ---
            if (purchases.isNotEmpty()) {
                item {
                    Text(
                        "Storico Acquisti",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp)
                    )
                }

                items(purchases) { purchase ->
                    HistoricalItemCard(
                        slotCode = "Acquisto",
                        date = dateFormatter.format(Date(purchase.dataAcquisto)),
                        cost = "€${"%.2f".format(purchase.prezzoPagato)}",
                        tools = listOf(purchase.toolName),
                        isRental = false
                    )
                }
            }
        }
    }
}

// --- SOTTO-COMPONENTI NECESSARI ---

@Composable
fun UserMainInfoCard(name: String, uid: String, email: String, totalRentals: Int, activeRentals: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("#User $uid", color = Color.Gray, fontSize = 14.sp)
            Text(email, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$totalRentals", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(" Noleggi", color = Color.Gray, fontSize = 16.sp)
                Spacer(Modifier.width(16.dp))
                Text("|", color = Color.LightGray, fontSize = 20.sp)
                Spacer(Modifier.width(16.dp))
                Text("$activeRentals", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(" In corso", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier,
    color: Color = Color.White,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(45.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = color),
        border = BorderStroke(1.dp, Color(0xFFFFD54F))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            if (icon != null) {
                Spacer(Modifier.width(4.dp))
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF1A237E))
            }
        }
    }
}

@Composable
fun SectionBox(text: String, isButtonActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    enabled = isButtonActive,
                    modifier = Modifier.weight(1f).height(35.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if(isButtonActive) Color(0xFFFFC107) else Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Rinnova", fontSize = 10.sp, color = if(isButtonActive) Color.White else Color.Gray)
                }
                Button(
                    onClick = {},
                    enabled = isButtonActive,
                    modifier = Modifier.weight(1f).height(35.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if(isButtonActive) Color(0xFFFFC107) else Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Gestione", fontSize = 10.sp, color = if(isButtonActive) Color.White else Color.Gray)
                }
            }
        }
    }
}

@Composable
fun HistoricalItemCard(slotCode: String, date: String, cost: String, tools: List<String>, isRental: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isRental) Icons.Default.ShoppingCart else Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isRental) Color(0xFFFFC107) else Color(0xFF4CAF50)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(slotCode, color = if (isRental) Color(0xFFFFC107) else Color(0xFF4CAF50), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                Text(date, fontSize = 11.sp, color = Color.Gray)
                Text("Tot: $cost", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8EAF6))
                    .padding(8.dp)
            ) {
                Column {
                    tools.forEach { tool ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(4.dp).background(Color(0xFF1A237E), CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text(tool, fontSize = 10.sp, color = Color(0xFF1A237E), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}