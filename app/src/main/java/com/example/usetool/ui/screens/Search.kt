package com.example.usetool.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.*
import com.example.usetool.ui.theme.*
import com.example.usetool.ui.viewmodel.SearchViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    navController: NavController,
    searchVm: SearchViewModel,
    useToolVm: UseToolViewModel
) {
    // CORREZIONE: Uso di collectAsStateWithLifecycle per efficienza e risparmio dati
    val query by searchVm.query.collectAsStateWithLifecycle()
    val filteredTools by searchVm.filteredTools.collectAsStateWithLifecycle()
    val maxDistance by searchVm.maxDistance.collectAsStateWithLifecycle()
    val selectedTypes by searchVm.selectedTypes.collectAsStateWithLifecycle()

    val tools by useToolVm.topTools.collectAsStateWithLifecycle()
    val lockers by useToolVm.lockers.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(useToolVm.errorMessage) {
        useToolVm.errorMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 🔍 SEARCH BAR FISSA IN ALTO
            OutlinedTextField(
                value = query,
                onValueChange = { searchVm.setQuery(it) },
                placeholder = { Text("Cerca") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            HomeSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(Modifier.height(16.dp))

            if (selectedTab == 0) {
                // --- VISTA LISTA ---
                // CORREZIONE: Rimosso verticalScroll esterno per usare le capacità native di LazyVerticalGrid
                Column {
                    Surface(
                        color = BlueLight,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Distanza massima: ${maxDistance.roundToInt()} km",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                            )
                            Slider(
                                value = maxDistance,
                                onValueChange = { searchVm.setMaxDistance(it) },
                                valueRange = 1f..20f,
                                colors = SliderDefaults.colors(
                                    thumbColor = BluePrimary,
                                    activeTrackColor = BluePrimary
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredTools) { tool ->
                            ToolCardMini(
                                tool = tool,
                                // Uso della funzione del ViewModel per la distanza
                                distanceKm = useToolVm.getDistanceForTool(tool.id)?.toFloat(),
                                onClick = {
                                    navController.navigate(NavRoutes.SchedaStrumento.createRoute(tool.id))
                                }
                            )
                        }
                    }
                }
            } else {
                // --- VISTA MAPPA ---
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    val allToolsDistinct = tools.distinctBy { it.name }
                    val columns = allToolsDistinct.chunked(3)
                    val listState = rememberLazyListState()

                    Surface(
                        color = YellowMedium,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Filtra per tipo", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))

                            LazyRow(state = listState, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(columns) { columnTools ->
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            Spacer(Modifier.height(8.dp))
                            DotsIndicator(totalDots = columns.size, visibleDot = listState.firstVisibleItemIndex)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        Box {
                            Image(
                                painter = painterResource(R.drawable.placeholder_map),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            lockers.forEachIndexed { index, locker ->
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = locker.name,
                                    tint = BluePrimary,
                                    modifier = Modifier
                                        .offset(x = (40 + index * 30).dp, y = (60 + index * 10).dp)
                                        .clickable {
                                            navController.navigate(NavRoutes.SchedaDistributore.createRoute(locker.id))
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DotsIndicator(totalDots: Int, visibleDot: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(if (index == visibleDot) 8.dp else 6.dp)
                    .background(
                        color = if (index == visibleDot) YellowPrimary else YellowPrimary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
fun ToolFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) YellowPrimary else YellowMedium,
        border = BorderStroke(1.dp, if (selected) YellowPrimary else Color.Black),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 14.sp
        )
    }
}