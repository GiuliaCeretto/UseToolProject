package com.example.usetool.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.BottomNavBar
import com.example.usetool.component.AppTopBar
import com.example.usetool.viewmodel.UseToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: UseToolViewModel
) {
    Scaffold(
        topBar = { AppTopBar(navController, "UseTool") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Ricerca strumenti (in sviluppo)")
        }
    }
}
