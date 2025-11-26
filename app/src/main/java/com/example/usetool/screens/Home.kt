@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.usetool.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.components.BottomNavBar
import com.example.usetool.model.Tool
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewModel.UseToolViewModel

@Composable
fun Home(navController: NavController, viewModel: UseToolViewModel) {

    val topTools = viewModel.tools.take(5)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("UseTool - Home") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                "Top strumenti",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(topTools) { tool ->
                    ToolCardSmall(
                        tool = tool,
                        onClick = {
                            navController.navigate(NavRoutes.Strumento.createRoute(tool.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ToolCardSmall(
    tool: Tool,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)   // mostra 2-3 strumenti a schermo
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG")
            }

            Spacer(Modifier.height(8.dp))

            Text(
                tool.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                if (tool.available) "Disponibile" else "Non disponibile",
                color = if (tool.available)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}


