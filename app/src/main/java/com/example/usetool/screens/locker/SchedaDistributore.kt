package com.example.usetool.screens.distributor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.UseToolViewModel
import com.example.usetool.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedaDistributoreScreen(
    navController: NavController,
    id: String,
    viewModel: UseToolViewModel,
    cartVM: CartViewModel
) {
    val locker = viewModel.findLockerById(id)

    Scaffold(
        topBar = { AppTopBar(navController, "UseTool") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text(locker?.address ?: "")
        }
    }
}
