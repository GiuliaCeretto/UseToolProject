package com.example.usetool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.data.dao.PurchaseEntity
import com.example.usetool.data.dao.RentalEntity
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitiroScreen(
    navController: NavController,
    purchases: List<PurchaseEntity>,
    rentals: List<RentalEntity>
) {
    // Stato per gestire i pulsanti disabilitati (mappa id -> ritirato)
    val retiredPurchases = remember { mutableStateMapOf<String, Boolean>() }
    val retiredRentals = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RITIRO STRUMENTI", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = BluePrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SEZIONE NOLEGGI ATTIVI ---
            if (rentals.isNotEmpty()) {
                item { SectionHeader("Noleggi Attivi", Icons.Default.Timer) }
                items(rentals) { rental ->
                    RitiroRentalCard(
                        rental = rental,
                        isRetired = retiredRentals[rental.id] ?: false,
                        onRitiroClick = { retiredRentals[rental.id] = true }
                    )
                }
            }

            // --- SEZIONE ACQUISTI ---
            if (purchases.isNotEmpty()) {
                item { SectionHeader("Acquisti da Ritirare", Icons.Default.Inventory2) }
                items(purchases) { purchase ->
                    RitiroPurchaseCard(
                        purchase = purchase,
                        isRetired = retiredPurchases[purchase.id] ?: false,
                        onRitiroClick = { retiredPurchases[purchase.id] = true }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("FINE RITIRO", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.DarkGray)
    }
}

// --- CARD PER NOLEGGI ---
@Composable
fun RitiroRentalCard(
    rental: RentalEntity,
    isRetired: Boolean,
    onRitiroClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(rental.toolName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = if (isRetired) "Ritirato con successo" else "Noleggio attivo",
                    color = if (isRetired) Color(0xFF4CAF50) else Color.Gray,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onRitiroClick,
                enabled = !isRetired,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRetired) Color.LightGray else Color(0xFFFFC107),
                    contentColor = if (isRetired) Color.Gray else Color.Black
                )
            ) {
                if (isRetired) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("RITIRATO")
                } else {
                    Text("RITIRA")
                }
            }
        }
    }
}

// --- CARD PER ACQUISTI ---
@Composable
fun RitiroPurchaseCard(
    purchase: PurchaseEntity,
    isRetired: Boolean,
    onRitiroClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(purchase.toolName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = if (isRetired) "Ritirato con successo" else "Pronto nel locker",
                    color = if (isRetired) Color(0xFF4CAF50) else Color.Gray,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = onRitiroClick,
                enabled = !isRetired,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRetired) Color.LightGray else Color(0xFFFFC107),
                    contentColor = if (isRetired) Color.Gray else Color.Black
                )
            ) {
                if (isRetired) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("RITIRATO")
                } else {
                    Text("RITIRA")
                }
            }
        }
    }
}
