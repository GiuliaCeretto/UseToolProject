package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.SearchToolCard
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.SearchViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun SearchScreen(
    navController: NavController,
    searchVm: SearchViewModel,
    useToolVm: UseToolViewModel
) {
    val query by searchVm.query.collectAsStateWithLifecycle()
    val filteredTools by searchVm.filteredTools.collectAsStateWithLifecycle()
    val maxDistance by searchVm.maxDistance.collectAsStateWithLifecycle()
    val selectedTypes by searchVm.selectedTypes.collectAsStateWithLifecycle()

    val tools by useToolVm.topTools.collectAsStateWithLifecycle()
    val lockers by useToolVm.lockers.collectAsStateWithLifecycle()
    val slots by useToolVm.slots.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }

    // TOOL DISPONIBILI + DISTANZA
    val displayTools = remember(filteredTools, maxDistance, slots) {
        filteredTools.filter { tool ->
            val dist = useToolVm.getDistanceForTool(tool.id) ?: 999.0
            val isAvailable = slots.any {
                it.toolId == tool.id &&
                        it.quantity > 0 &&
                        it.status == "DISPONIBILE"
            }
            isAvailable && dist <= maxDistance.toDouble()
        }
    }

    // LOCKER FILTRATI PER MAPPA
    val activeFilters = selectedTypes.filter { it.value }.keys
    val displayLockers = remember(lockers, slots, activeFilters) {
        if (activeFilters.isEmpty()) lockers
        else lockers.filter { locker ->
            slots.any {
                it.lockerId == locker.id &&
                        activeFilters.contains(it.toolId) &&
                        it.quantity > 0
            }
        }
    }

    Scaffold(containerColor = Color(0xFFF8F9FA)) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // --- BARRA DI RICERCA ---
                var lockerQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = if(selectedTab == 0) query else lockerQuery,
                    onValueChange = {
                        if(selectedTab == 0) searchVm.setQuery(it)
                        else lockerQuery = it
                    },
                    placeholder = { Text(if(selectedTab == 0) "Cerca attrezzi" else "Cerca distributori") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.height(20.dp))

                if (selectedTab == 0) {
                    // --- TAB LISTA ATTREZZI ---
                    Surface(
                        color = BlueLight,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Distanza massima: ${maxDistance.roundToInt()} km",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Slider(
                                value = maxDistance,
                                onValueChange = { searchVm.setMaxDistance(it) },
                                valueRange = 1f..100f,
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
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayTools) { tool ->
                            SearchToolCard(
                                tool = tool,
                                calculatedDistance = useToolVm.getDistanceForTool(tool.id),
                                onClick = {
                                    navController.navigate(NavRoutes.SchedaStrumento.createRoute(tool.id))
                                }
                            )
                        }
                    }
                } else {
                    // --- TAB MAPPA ---
                    val filteredLockers = remember(displayLockers, lockerQuery) {
                        if (lockerQuery.isBlank()) displayLockers
                        else displayLockers.filter { it.name.contains(lockerQuery, ignoreCase = true) }
                    }

                    val allToolsList = tools.distinctBy { it.name }
                    val columnsTools = allToolsList.chunked(3)

                    Surface(
                        color = YellowMedium,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Filtra per attrezzo",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(columnsTools) { columnTools ->
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        columnTools.forEach { tool ->
                                            ToolFilterChip(
                                                text = tool.name,
                                                selected = selectedTypes[tool.id] == true,
                                                onClick = { searchVm.toggleType(tool.id) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Mappa con AsyncImage per caricamento dinamico
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    ) {
                        AsyncImage(
                            model = "https://example.com/api/static-map-url", // Sostituire con URL reale
                            contentDescription = "Mappa",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(R.drawable.placeholder_map),
                            error = painterResource(R.drawable.placeholder_map)
                        )

                        filteredLockers.forEachIndexed { index, locker ->
                            LockerPin(
                                locker = locker,
                                index = index,
                                totalLockers = filteredLockers.size,
                                onClick = { navController.navigate(NavRoutes.SchedaDistributore.createRoute(locker.id)) }
                            )
                        }
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }

            FloatingSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun LockerPin(
    locker: LockerEntity,
    index: Int,
    totalLockers: Int,
    onClick: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val columns = 3
        val rows = ((totalLockers + columns - 1) / columns).coerceAtLeast(1)
        val colIndex = index % columns
        val rowIndex = index / columns

        val horizontalMargin = 0.1f
        val verticalMargin = 0.1f
        val baseX = horizontalMargin + colIndex * ((0.7f - 2 * horizontalMargin) / (columns - 1))
        val rowSpacingFactor = 0.7f
        val baseY = verticalMargin + rowIndex * ((rowSpacingFactor - 2 * verticalMargin) / (rows - 1).coerceAtLeast(1))

        val xPercent = (baseX + Random.nextFloat() * 0.03f - 0.015f).coerceIn(horizontalMargin, 0.75f)
        val yPercent = (baseY + Random.nextFloat() * 0.02f - 0.01f).coerceIn(verticalMargin, rowSpacingFactor)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(x = maxWidth * xPercent, y = maxHeight * yPercent)
                .clickable { onClick() }
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier
            .padding(bottom = 32.dp)
            .height(50.dp)
            .width(160.dp)
            .shadow(8.dp, CircleShape),
        shape = CircleShape,
        color = Color.White
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwitcherButton(
                icon = Icons.AutoMirrored.Filled.List,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            SwitcherButton(
                icon = Icons.Default.LocationOn,
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SwitcherButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(if (isSelected) YellowPrimary else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.Black else Color.Gray
        )
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
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}