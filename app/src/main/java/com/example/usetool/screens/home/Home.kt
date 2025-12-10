package com.example.usetool.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.model.Tool
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.component.ToolCardSmall
import com.example.usetool.viewmodel.CartViewModel
import com.example.usetool.viewmodel.UseToolViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, vm: UseToolViewModel, cartVM: CartViewModel) {
    val topTools by vm.topTools.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = { AppTopBar(navController, "UseTool") },
        bottomBar = { BottomNavBar(navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            Text("Top strumenti", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            // Carosello strumenti
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(topTools) { tool ->
                    ToolCardSmall(
                        tool = tool,
                        onClick = {
                            navController.navigate(NavRoutes.ToolDetailFiltered.buildRoute(tool.id))
                        },
                        onAdd = {
                            cartVM.add(tool, null)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("${tool.name} aggiunto al carrello")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Riepilogo noleggio in corso - placeholder
            val rented = topTools.firstOrNull { !it.available } // esempio mock
            if (rented != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Noleggio in corso", style = MaterialTheme.typography.titleMedium)
                        Text(rented.name)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            navController.navigate(NavRoutes.RentedToolDetail.buildRoute(rented.id))
                        }) { Text("Vai alla scheda noleggiata") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mappa placeholder
            Text("Distributori vicini", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { navController.navigate(NavRoutes.DistributorDetail.buildRoute("d1")) }
            ) {
                Text("Mappa placeholder (clicca per esempio)", modifier = Modifier.padding(12.dp))
            }
        }
    }
}
