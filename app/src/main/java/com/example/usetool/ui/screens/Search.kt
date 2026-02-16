package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.SearchToolCard
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.SearchViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Lista, 1: Mappa

    // --- FILTRAGGIO AVANZATO ---
    // Mostriamo solo i tool che hanno almeno uno slot DISPONIBILE con quantity > 0
    val displayTools = remember(filteredTools, maxDistance, slots) {
        filteredTools.filter { tool ->
            val dist = useToolVm.getDistanceForTool(tool.id) ?: 999.0
            val isAvailable = slots.any { it.toolId == tool.id && it.quantity > 0 && it.status == "DISPONIBILE" }
            isAvailable && dist <= maxDistance.toDouble()
        }
    }

    val activeFilters = selectedTypes.filter { it.value }.keys
    val displayLockers = remember(lockers, slots, activeFilters) {
        if (activeFilters.isEmpty()) lockers
        else lockers.filter { locker ->
            slots.any { it.lockerId == locker.id && activeFilters.contains(it.toolId) && it.quantity > 0 }
        }
    }

    Scaffold(containerColor = Color(0xFFF8F9FA)) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchHeader(query, { searchVm.setQuery(it) }, selectedTab == 0)

                if (selectedTab == 0) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DistanceSlider(maxDistance) { searchVm.setMaxDistance(it) }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                        ) {
                            items(displayTools) { tool ->
                                SearchToolCard(
                                    tool = tool,
                                    calculatedDistance = useToolVm.getDistanceForTool(tool.id),
                                    onClick = { navController.navigate(NavRoutes.SchedaStrumento.createRoute(tool.id)) }
                                )
                            }
                        }
                    }
                } else {
                    MapSection(
                        tools = tools,
                        selectedTypes = selectedTypes,
                        onToggle = { searchVm.toggleType(it) },
                        displayLockers = displayLockers,
                        onLockerClick = { navController.navigate(NavRoutes.SchedaDistributore.createRoute(it)) }
                    )
                }
            }

            FloatingSwitcher(selectedTab, { selectedTab = it }, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun MapSection(
    tools: List<ToolEntity>,
    selectedTypes: Map<String, Boolean>,
    onToggle: (String) -> Unit,
    displayLockers: List<LockerEntity>,
    onLockerClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Sezione Filtri a Pagine
        val toolPages = remember(tools) { tools.distinctBy { it.name }.chunked(6) }
        if (toolPages.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { toolPages.size })
            val scope = rememberCoroutineScope()

            Surface(
                color = YellowPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Filtra per Categoria", fontWeight = FontWeight.Bold, color = BluePrimary)
                    Spacer(Modifier.height(12.dp))

                    HorizontalPager(state = pagerState) { page ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            toolPages[page].chunked(3).forEach { columnItems ->
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    columnItems.forEach { tool ->
                                        FilterRowItem(tool.name, selectedTypes[tool.id] == true) { onToggle(tool.id) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mappa
        Surface(
            modifier = Modifier.fillMaxWidth().height(400.dp).padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
        ) {
            Box {
                Image(painter = painterResource(R.drawable.placeholder_map), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

                displayLockers.forEachIndexed { i, locker ->
                    LockerPin(locker, i) { onLockerClick(locker.id) }
                }
            }
        }
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun LockerPin(locker: LockerEntity, index: Int, onClick: () -> Unit) {
    // Posizionamento pin fittizio basato su index per la demo
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = (50 + (index % 2) * 150).dp, y = (70 + (index / 2) * 100).dp)
            .clickable { onClick() }
    ) {
        Image(painter = painterResource(R.drawable.pin), contentDescription = null, modifier = Modifier.size(36.dp))
        Surface(color = BluePrimary, shape = RoundedCornerShape(4.dp)) {
            Text(locker.name, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(4.dp))
        }
    }
}

@Composable
fun FilterRowItem(text: String, selected: Boolean, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onToggle() }
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = YellowPrimary),
            modifier = Modifier.scale(0.7f)
        )
        Text(text, fontSize = 11.sp, maxLines = 1, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
    }
}


@Composable
fun SearchHeader(query: String, onQueryChange: (String) -> Unit, showSearch: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = if (showSearch) "Cerca Attrezzi" else "Esplora Mappa",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = BluePrimary
        )
        if (showSearch) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Cacciaviti, Trapani...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = YellowPrimary) },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = YellowPrimary
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun DistanceSlider(value: Float, onValueChange: (Float) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Raggio d'azione", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("${value.roundToInt()} km", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 1f..100f,
                colors = SliderDefaults.colors(thumbColor = YellowPrimary, activeTrackColor = BluePrimary)
            )
        }
    }
}

@Composable
fun FloatingSwitcher(selectedTab: Int, onTabSelected: (Int) -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.padding(bottom = 32.dp).height(50.dp).width(160.dp).shadow(8.dp, CircleShape),
        shape = CircleShape,
        color = Color.White
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            SwitcherButton(
                icon = Icons.AutoMirrored.Filled.List,
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            SwitcherButton(
                icon = null, // Placeholder per pin icon
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f),
                isPin = true
            )
        }
    }
}

@Composable
fun SwitcherButton(icon: Any?, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier, isPin: Boolean = false) {
    Box(
        modifier = modifier.fillMaxHeight().clip(CircleShape)
            .background(if (isSelected) YellowPrimary else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isPin) {
            Icon(painter = painterResource(R.drawable.pin), contentDescription = null, modifier = Modifier.size(22.dp), tint = if (isSelected) Color.Black else Color.Gray)
        } else if (icon is androidx.compose.ui.graphics.vector.ImageVector) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) Color.Black else Color.Gray)
        }
    }
}