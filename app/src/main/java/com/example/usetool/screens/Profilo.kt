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
fun Profilo(navController: NavController, viewModel: UseToolViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Profilo Utente") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Nome utente: Demo User")
            Text("Saldo: €50")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Torna alla Home")
            }
        }
    }
}
