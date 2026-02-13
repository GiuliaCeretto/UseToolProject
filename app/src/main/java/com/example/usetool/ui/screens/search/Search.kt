package com.example.usetool.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.component.*
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.*
import com.example.usetool.viewmodel.UseToolViewModel
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

        // 🔁 SWITCHER
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

            Surface(
                color = BlueLight,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        "Distanza massima: ${maxDistance.roundToInt()} km",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                    )

                    Slider(
                        value = maxDistance,
                        onValueChange = { maxDistance = it },
                        valueRange = 1f..20f,
                        steps = 0,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = BluePrimary,
                            activeTrackColor = BluePrimary,
                            inactiveTrackColor = BluePrimary.copy(alpha = 0.3f)
                        )
                    )
                }
            }

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
                    ToolCardMini(
                        tool = tool,
                        distanceKm = viewModel.getDistanceForTool(tool.id)?.toFloat(),
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

            val allTools = tools.distinctBy { it.name }
            val columns = allTools.chunked(3)
            val listState = rememberLazyListState()

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

            // 🎛️ FILTRI
            Surface(
                color = YellowMedium,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

                    Text(
                        "Filtra per tipo",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        color = Color.Black
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyRow(
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(columns) { columnTools ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                columnTools.forEach { tool ->
                                    ToolFilterChip(
                                        text = tool.name,
                                        selected = selectedTypes[tool.id] == true,
                                        onClick = {
                                            selectedTypes[tool.id] =
                                                !(selectedTypes[tool.id] ?: false)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    DotsIndicator(
                        totalDots = columns.size,
                        visibleDot = listState.firstVisibleItemIndex
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🗺️ MAPPA CON BORDI STONDATI
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.placeholder_map),
                        contentDescription = "Mappa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
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
                                tint = BluePrimary,
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
}

// 🔁 SWITCHER

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
            SwitcherItem(
                text = "Lista",
                selected = selectedTab == 0,
                selectedColor = BluePrimary,
            ) { onTabSelected(0) }

            SwitcherItem(
                text = "Mappa",
                selected = selectedTab == 1,
                selectedColor = YellowPrimary
            ) { onTabSelected(1) }
        }
    }
}

@Composable
private fun SwitcherItem(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) selectedColor else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp,),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// 🔘 DOTS INDICATOR (solo mappa)

@Composable
fun DotsIndicator(
    totalDots: Int,
    visibleDot: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == visibleDot) 8.dp else 6.dp)
                    .background(
                        color = if (index == visibleDot)
                            YellowPrimary
                        else
                            YellowPrimary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun ToolFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) YellowPrimary else YellowMedium,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) YellowPrimary else Color.Black
        ),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
            color = Color.Black,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
        )
    }
}



