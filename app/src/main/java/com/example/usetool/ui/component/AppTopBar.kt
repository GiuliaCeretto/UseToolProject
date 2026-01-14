package com.example.usetool.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavController, title: String = "UseTool") {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { /* open settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Impostazioni")
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(NavRoutes.Carrello.route) }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrello")
            }
        }
    )
}
