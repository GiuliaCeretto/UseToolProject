package com.example.usetool.screens.tool

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.BottomNavBar
import com.example.usetool.component.AppTopBar
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.UseToolViewModel
import com.example.usetool.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaStrumentoScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    val tool = viewModel.findToolById(id)

    Scaffold(
        topBar = { AppTopBar(navController, "UseTool") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            Text(tool?.description ?: "Nessuna descrizione")

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                tool?.let { cartVM.add(it, null) }
            }) {
                Text("Aggiungi al carrello")
            }
        }
    }
}

