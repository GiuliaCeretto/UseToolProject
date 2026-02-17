package com.example.usetool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.data.dao.PurchaseEntity
import com.example.usetool.data.dao.RentalEntity
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.viewmodel.OrderViewModel
import com.example.usetool.ui.viewmodel.UserViewModel
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitiroScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    orderViewModel: OrderViewModel,
    lockerId: Int
) {
    // Recupero UID utente (corretto con userProfile?.uid)
    val userProfile by userViewModel.userProfile.collectAsStateWithLifecycle()
    val userId = userProfile?.uid ?: ""

    // Osserviamo i dati reali dal database ordini
    val allPurchases by orderViewModel.localPurchases.collectAsStateWithLifecycle()
    val allRentals by orderViewModel.localRentals.collectAsStateWithLifecycle()

    // Filtriamo gli oggetti che devono essere mostrati in questa schermata
    val purchasesToRetrieve = remember(allPurchases, lockerId) {
        allPurchases.filter {
            it.lockerId == lockerId.toString() && it.dataRitiroEffettiva == null
        }
    }

    val rentalsToRetrieve = remember(allRentals, lockerId) {
        allRentals.filter {
            it.lockerId == lockerId.toString() && it.dataRiconsegnaEffettiva == null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("RITIRO LOCKER #$lockerId", fontWeight = FontWeight.Bold) },
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
            // --- SEZIONE ACQUISTI ---
            if (purchasesToRetrieve.isNotEmpty()) {
                item { SectionHeader("Prodotti Acquistati", Icons.Default.Inventory2) }
                items(purchasesToRetrieve) { purchase ->
                    RitiroPurchaseCard(
                        purchase = purchase,
                        onRitiroClick = { orderViewModel.confirmPurchasePickup(userId, purchase.id) }
                    )
                }
            }

            // --- SEZIONE NOLEGGI ---
            if (rentalsToRetrieve.isNotEmpty()) {
                item { SectionHeader("Strumenti a Noleggio", Icons.Default.Timer) }
                items(rentalsToRetrieve) { rental ->
                    RitiroRentalCard(
                        rental = rental,
                        onRitiroClick = { orderViewModel.confirmStartRental(userId, rental.id) }
                    )
                }
            }

            // --- MESSAGGIO VUOTO ---
            if (purchasesToRetrieve.isEmpty() && rentalsToRetrieve.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Nessun oggetto da ritirare qui.", color = Color.Gray)
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("TORNA ALLA HOME", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.DarkGray)
    }
}

@Composable
fun RitiroPurchaseCard(purchase: PurchaseEntity, onRitiroClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(purchase.toolName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Acquisto definitivo", color = Color.Gray, fontSize = 12.sp)
            }
            Button(
                onClick = onRitiroClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("RITIRA")
            }
        }
    }
}

@Composable
fun RitiroRentalCard(rental: RentalEntity, onRitiroClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(rental.toolName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Noleggio temporaneo", color = Color.Gray, fontSize = 12.sp)
            }
            Button(
                onClick = onRitiroClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("AVVIA NOLEGGIO", color = Color.Black)
            }
        }
    }
}