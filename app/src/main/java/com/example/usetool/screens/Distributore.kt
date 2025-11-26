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
fun Distributore(navController: NavController, viewModel: UseToolViewModel, id: String) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Distributore #$id") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Informazioni sul distributore $id")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.navigate(NavRoutes.Strumento.createRoute("1")) }) {
                Text("Vai a Strumento esempio")
            }
        }
    }
}
