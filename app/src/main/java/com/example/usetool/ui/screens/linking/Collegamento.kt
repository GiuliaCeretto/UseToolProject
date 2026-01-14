@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.usetool.ui.screens.linking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.BottomNavBar
import com.example.usetool.ui.viewmodel.UseToolViewModel

@Composable
fun CollegamentoScreen(
    navController: NavController,
    viewModel: UseToolViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Collegamento Locker") }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {

            Text("Associa il tuo telefono a un locker", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Qui in futuro ci sarà la logica Bluetooth / NFC
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scansiona RFID / NFC")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // eventuale pairing manuale
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Inserisci Codice Locker")
            }
        }
    }
}
