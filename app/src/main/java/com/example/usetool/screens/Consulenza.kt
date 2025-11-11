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
fun Consulenza(navController: NavController, viewModel: UseToolViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Consulenza") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Rubrica esperti e consigli personalizzati.")
        }
    }
}
