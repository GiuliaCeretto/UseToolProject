package com.example.usetool.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.BottomNavBar
import com.example.usetool.component.AppTopBar
import com.example.usetool.ui.viewmodel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfiloScreen(
    navController: NavController,
    viewModel: UseToolViewModel
) {
    Scaffold(
        topBar = { AppTopBar(navController, "UseTool") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Nome utente: Demo User")
            Text("Saldo: €50")
        }
    }
}

