package com.example.usetool.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewModel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun Home(navController: NavController, viewModel: UseToolViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("UseTool - Home") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { navController.navigate(NavRoutes.Ricerca.route) }) {
                Text("Ricerca Strumenti")
            }
            Button(onClick = { navController.navigate(NavRoutes.MieiStrumenti.route) }) {
                Text("I miei strumenti")
            }
            Button(onClick = { navController.navigate(NavRoutes.Profilo.route) }) {
                Text("Profilo Utente")
            }

            Divider()
            Text("Top strumenti:")
            viewModel.tools.forEach { tool ->
                Button(onClick = { navController.navigate(NavRoutes.Strumento.createRoute(tool.id)) }) {
                    Text("${tool.name} - ${if (tool.available) "Disponibile" else "Non disponibile"}")
                }
            }
        }
    }
}
