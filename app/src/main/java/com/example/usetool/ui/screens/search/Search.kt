package com.example.usetool.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.component.*
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: UseToolViewModel
) {
    val tools by viewModel.topTools.collectAsState(initial = emptyList())
    val lockers by viewModel.lockers.collectAsState(initial = emptyList())

    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var maxDistance by remember { mutableStateOf(5f) }
    val selectedTypes = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = { AppTopBar(navController, "Cerca") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // SEARCH BAR
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Cerca") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            /* 🔁 SWITCHER */
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HomeSwitcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }

            Spacer(Modifier.height(20.dp))

            // LISTA
            if (selectedTab == 0) {

                Text(
                    "Distanza da te: ${maxDistance.roundToInt()} km",
                    style = MaterialTheme.typography.titleMedium
                )


                Slider(
                    value = maxDistance,
                    onValueChange = { maxDistance = it },
                    valueRange = 1f..20f,
                    steps = 18
                )

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .height(510.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        tools.filter { it.name?.contains(query, ignoreCase = true) == true }
                    ) { tool ->
                        ToolCardMini(
                            tool = tool,
                            distanceKm = 5f,
                            onClick = {
                                navController.navigate(
                                    NavRoutes.SchedaStrumento.createRoute(tool.id)
                                )
                            }
                        )
                    }
                }
            }

            // MAPPA
            else {
                Text(
                    "Filtra per tipo",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                val allTools = tools.distinctBy { it.name }.take(9)
                val columns = allTools.chunked(3) // ogni colonna ha max 3 chip

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(columns) { columnTools ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            columnTools.forEach { tool ->
                                FilterChip(
                                    selected = selectedTypes[tool.id] == true,
                                    onClick = {
                                        selectedTypes[tool.id] = !(selectedTypes[tool.id] ?: false)
                                    },
                                    label = { Text(tool.name) }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    // MAPPA
                    val mapPainter = runCatching { painterResource(R.drawable.placeholder_map) }.getOrNull()
                    mapPainter?.let {
                        Image(
                            painter = it,
                            contentDescription = "Mappa",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // PIN CLICCABILI
                    lockers.forEachIndexed { index, locker ->
                        Column(
                            modifier = Modifier
                                .offset(
                                    x = (40 + index * 80).dp,
                                    y = (60 + index * 30).dp
                                )
                                .clickable {
                                    navController.navigate(
                                        NavRoutes.SchedaDistributore.createRoute(locker.id)
                                    )
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = locker.name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    locker.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }


        }
    }
}

// SWITCHER

@Composable
fun HomeSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwitcherItem("Lista", selectedTab == 0) {
                onTabSelected(0)
            }
            SwitcherItem("Mappa", selectedTab == 1) {
                onTabSelected(1)
            }
        }
    }
}

@Composable
private fun SwitcherItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected)
            MaterialTheme.colorScheme.primary
        else
            Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

