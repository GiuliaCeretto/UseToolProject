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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfiloScreen(
    navController: NavController,
    userVm: UserViewModel
) {
    // Osserviamo i dati reattivi da Room via UserViewModel
    val profile by userVm.userProfile.collectAsStateWithLifecycle()
    val allRentals by userVm.rentals.collectAsStateWithLifecycle()
    val purchases by userVm.purchases.collectAsStateWithLifecycle()

    // Filtriamo i noleggi attivi per il box di gestione rapida
    val activeRentalsCount = remember(allRentals) {
        allRentals.count { it.statoNoleggio == "ATTIVO" }
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY) }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp, start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER INFO UTENTE ---
            item {
                Text(
                    text = "Il mio Profilo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = BluePrimary
                )

                UserAvatarSection()

                Spacer(modifier = Modifier.height(16.dp))

                UserMainInfoCard(
                    name = "${profile?.nome ?: "Utente"} ${profile?.cognome ?: ""}",
                    uid = profile?.uid?.take(8)?.uppercase() ?: "...",
                    email = profile?.email ?: "Email non disponibile",
                    totalPurchases = purchases.size,
                    activeRentals = activeRentalsCount
                )
            }

            // --- BOX GESTIONE NOLEGGIO ATTIVO ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionBox(
                    text = if (activeRentalsCount > 0)
                        "Hai $activeRentalsCount noleggio/i attivo/i"
                    else "Nessun noleggio in corso",
                    isButtonActive = activeRentalsCount > 0,
                    onClickManage = { navController.navigate(NavRoutes.Home.route) }
                )
            }

            // --- SEZIONE STORICO NOLEGGI (Priorità Rental) ---
            if (allRentals.isNotEmpty()) {
                item { SectionHeader("Storico Noleggi") }

                items(allRentals) { rental ->
                    val isRunning = rental.statoNoleggio == "ATTIVO"
                    HistoricalItemCard(
                        title = rental.toolName,
                        date = "Data: ${dateFormatter.format(Date(rental.dataInizio))}",
                        cost = "€ ${"%.2f".format(rental.costoTotale)}",
                        statusLabel = if (isRunning) "IN CORSO" else "CONCLUSO",
                        statusColor = if (isRunning) Color(0xFF4CAF50) else Color.Gray,
                        icon = if (isRunning) Icons.Default.PlayArrow else Icons.Default.History
                    )
                }
            }

            // --- SEZIONE STORICO ACQUISTI ---
            if (purchases.isNotEmpty()) {
                item { SectionHeader("Storico Acquisti") }

                items(purchases) { purchase ->
                    HistoricalItemCard(
                        title = purchase.toolName,
                        date = "Data: ${dateFormatter.format(Date(purchase.dataAcquisto))}",
                        cost = "€ ${"%.2f".format(purchase.prezzoPagato)}",
                        statusLabel = "ACQUISTATO",
                        statusColor = BluePrimary,
                        icon = Icons.Default.ShoppingCart
                    )
                }
            }

            // --- PULSANTE LOGOUT ---
            item {
                Spacer(modifier = Modifier.height(32.dp))
                ActionButton(
                    text = "Disconnetti",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    color = Color(0xFFFFEBEE),
                    onClick = { userVm.performFullLogout() }
                )
            }
        }
    }
}

@Composable
fun UserAvatarSection() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, BluePrimary.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            modifier = Modifier.size(60.dp),
            tint = BluePrimary
        )
    }
}
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = BluePrimary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun UserMainInfoCard(name: String, uid: String, email: String, totalPurchases: Int, activeRentals: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("ID: $uid", color = Color.Gray, fontSize = 12.sp)
            Text(email, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn(count = totalPurchases.toString(), label = "Acquisti")
                Box(modifier = Modifier.size(1.dp, 30.dp).background(Color(0xFFE0E0E0)))
                StatColumn(count = activeRentals.toString(), label = "In Corso")
            }
        }
    }
}

@Composable
fun StatColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = BluePrimary)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun HistoricalItemCard(title: String, date: String, cost: String, statusLabel: String, statusColor: Color, icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(date, fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = statusLabel.uppercase(),
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Text(cost, fontWeight = FontWeight.Black, fontSize = 16.sp, color = BluePrimary)
        }
    }
}

@Composable
fun SectionBox(text: String, isButtonActive: Boolean, onClickManage: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isButtonActive) Color(0xFFFFF8E1) else Color.White
        ),
        border = BorderStroke(1.dp, if (isButtonActive) Color(0xFFFFD54F) else Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isButtonActive) Icons.Default.Info else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isButtonActive) Color(0xFFFBC02D) else Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            }

            if (isButtonActive) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClickManage,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text("GESTISCI NOLEGGI", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector?, modifier: Modifier, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFFD32F2F))
            Spacer(Modifier.width(10.dp))
        }
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
    }
}