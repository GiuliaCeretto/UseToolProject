package com.example.usetool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.usetool.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.usetool.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.usetoollogo),
                contentDescription = "Logo UseTool",
                modifier = Modifier.size(width = 160.dp, height = 55.dp)
            )
        },
        actions = {
            IconButton(onClick = { navController.navigate(NavRoutes.Carrello.route) }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrello")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

