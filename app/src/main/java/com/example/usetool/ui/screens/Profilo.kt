package com.example.usetool.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.ui.component.BottomNavBar
import com.example.usetool.ui.component.AppTopBar
import com.example.usetool.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfiloScreen(
    navController: NavController,
    userVm: UserViewModel // Aggiunto per accedere ai dati reali dell'utente
) {
    // Osserviamo il profilo, i noleggi e gli acquisti in modo efficiente
    val profile by userVm.userProfile.collectAsStateWithLifecycle()
    val rentals by userVm.rentals.collectAsStateWithLifecycle()
    val purchases by userVm.purchases.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(navController) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SEZIONE INFORMAZIONI UTENTE
            item {
                Text(
                    text = "Il mio Profilo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Utente: ${profile?.nome ?: "Caricamento..."} ${profile?.cognome ?: ""}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Email: ${profile?.email ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Indirizzo: ${profile?.indirizzo ?: "Non specificato"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // SEZIONE NOLEGGI ATTIVI
            item {
                Text(text = "Noleggi attivi", style = MaterialTheme.typography.titleLarge)
            }

            val activeRentals = rentals.filter { it.statoNoleggio == "ATTIVO" }
            if (activeRentals.isEmpty()) {
                item { Text("Nessun noleggio in corso", style = MaterialTheme.typography.bodySmall) }
            } else {
                items(activeRentals) { rental ->
                    ListItem(
                        headlineContent = { Text(rental.toolName) },
                        supportingContent = { Text("Scadenza: ${rental.dataFinePrevista}") },
                        trailingContent = { Text("€${rental.costoTotale}", fontWeight = FontWeight.Bold) }
                    )
                    HorizontalDivider()
                }
            }

            // SEZIONE CRONOLOGIA ACQUISTI
            item {
                Text(text = "Cronologia acquisti", style = MaterialTheme.typography.titleLarge)
            }

            if (purchases.isEmpty()) {
                item { Text("Nessun acquisto effettuato", style = MaterialTheme.typography.bodySmall) }
            } else {
                items(purchases) { purchase ->
                    ListItem(
                        headlineContent = { Text(purchase.toolName) },
                        supportingContent = { Text("Data: ${purchase.dataAcquisto}") },
                        trailingContent = { Text("€${purchase.prezzoPagato}") }
                    )
                }
            }

            // TASTO LOGOUT
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { userVm.performFullLogout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Esci dall'account")
                }
            }
        }
    }
}