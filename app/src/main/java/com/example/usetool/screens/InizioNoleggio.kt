package com.example.usetool.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.viewModel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InizioNoleggio(navController: NavController, viewModel: UseToolViewModel, toolId: String) {
    val tool = viewModel.tools.find { it.id == toolId }
    Scaffold(topBar = { TopAppBar(title = { Text("Avvio noleggio") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Riepilogo prenotazione per: ${tool?.name ?: "Sconosciuto"}")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Conferma e torna indietro")
            }
        }
    }
}
