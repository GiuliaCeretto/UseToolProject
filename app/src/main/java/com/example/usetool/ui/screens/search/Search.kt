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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.component.ToolCardMini
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlin.math.roundToInt

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

    // 🔎 FILTRO TOOL
    val filteredTools = tools.filter { tool ->
        val matchesQuery = tool.name.contains(query, ignoreCase = true)
        val distance = viewModel.getDistanceForTool(tool.id)
        val matchesDistance = distance != null && distance <= maxDistance
        matchesQuery && matchesDistance
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 🔍 SEARCH BAR
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Cerca") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        // 🔁 SWITCHER LISTA / MAPPA
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HomeSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        Spacer(Modifier.height(20.dp))

        // 📋 LISTA
        if (selectedTab == 0) {

            Text(
                "Distanza massima: ${maxDistance.roundToInt()} km",
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
                    .height(520.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTools) { tool ->
                    val distance =
                        viewModel.getDistanceForTool(tool.id)?.toFloat()

                    ToolCardMini(
                        tool = tool,
                        distanceKm = distance,
                        onClick = {
                            navController.navigate(
                                NavRoutes.SchedaStrumento.createRoute(tool.id)
                            )
                        }
                    )
                }
            }
        }

        // 🗺️ MAPPA
        else {

            Text(
                "Filtra per tipo",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            val allTools = tools.distinctBy { it.name }
            val columns = allTools.chunked(3)

            val filteredLockers by remember(selectedTypes, lockers) {
                derivedStateOf {
                    val selectedToolIds =
                        selectedTypes.filter { it.value }.keys

                    if (selectedToolIds.isEmpty()) lockers
                    else lockers.filter { locker ->
                        selectedToolIds.all { it in locker.toolsAvailable }
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(columns) { columnTools ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        columnTools.forEach { tool ->
                            FilterChip(
                                selected = selectedTypes[tool.id] == true,
                                onClick = {
                                    selectedTypes[tool.id] =
                                        !(selectedTypes[tool.id] ?: false)
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
                Image(
                    painter = painterResource(R.drawable.placeholder_map),
                    contentDescription = "Mappa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                filteredLockers.forEachIndexed { index, locker ->
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
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                            )
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

