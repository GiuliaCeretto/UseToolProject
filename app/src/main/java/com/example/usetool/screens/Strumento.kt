package com.example.usetool.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.components.BottomNavBar
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewModel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Strumento(navController: NavController, viewModel: UseToolViewModel, id: String) {
    val tool = viewModel.tools.find { it.id == id }

    Scaffold(
        topBar = { TopAppBar(title = { Text(tool?.name ?: "Strumento") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(tool?.description ?: "Nessuna descrizione")
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate(NavRoutes.InizioNoleggio.createRoute(id))
            }) {
                Text("Prenota il noleggio")
            }
        }
    }
}
