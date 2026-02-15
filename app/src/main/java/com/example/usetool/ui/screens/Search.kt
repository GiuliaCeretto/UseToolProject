package com.example.usetool.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.SearchToolCard
import com.example.usetool.ui.theme.BlueLight
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.YellowMedium
import com.example.usetool.ui.theme.YellowPrimary
import com.example.usetool.ui.viewmodel.SearchViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlin.math.roundToInt


@SuppressLint("FrequentlyChangingValue")
@Composable
fun SearchScreen(
    navController: NavController,
    searchVm: SearchViewModel,
    useToolVm: UseToolViewModel
) {
    // ... logica degli stati (query, filteredTools, ecc.) invariata ...
    val query by searchVm.query.collectAsStateWithLifecycle()
    val filteredTools by searchVm.filteredTools.collectAsStateWithLifecycle()
    val maxDistance by searchVm.maxDistance.collectAsStateWithLifecycle()
    val selectedTypes by searchVm.selectedTypes.collectAsStateWithLifecycle()
    val tools by useToolVm.topTools.collectAsStateWithLifecycle()
    val lockers by useToolVm.lockers.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    val displayTools = remember(filteredTools, maxDistance) {
        filteredTools.filter { tool ->
            val dist = useToolVm.getDistanceForTool(tool.id)
            dist == null || dist <= maxDistance.toDouble()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // --- BARRA RICERCA ---
                OutlinedTextField(
                    value = query,
                    onValueChange = { searchVm.setQuery(it) },
                    placeholder = { Text("Cerca", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // --- SWITCHER ALTO (Attrezzo / Distributore) ---
                // Nota: rinominato per coerenza con image_5aa6ab.png
                Switcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                Spacer(Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // --- VISTA LISTA (DISTRIBUTORE / LISTA) ---
                    // ... (Codice della lista invariato) ...
                    Column {
                        Surface(
                            color = BlueLight.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Distanza da te", fontWeight = FontWeight.Bold)
                                    Text("Km 0-${maxDistance.roundToInt()}", color = Color.Gray)
                                }
                                Slider(
                                    value = maxDistance,
                                    onValueChange = { searchVm.setMaxDistance(it) },
                                    valueRange = 1f..30f,
                                    colors = SliderDefaults.colors(thumbColor = BluePrimary, activeTrackColor = BluePrimary)
                                )
                                Text("Scegli la distanza", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
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
                    // --- VISTA MAPPA (VICINI A TE / ATTREZZO) ---
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        // PANNELLO FILTRI GIALLO (Fedele a image_8d0828.png)
                        Surface(
                            color = YellowMedium.copy(alpha = 0.3f), // Giallo tenue come in Figma
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                LazyRow(
                                    state = listState,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    // Organizzazione in colonne da 3 elementi ciascuna (image_8d0828.png)
                                    items(tools.distinctBy { it.name }.chunked(6)) { toolPage ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                            toolPage.chunked(3).forEach { columnItems ->
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    columnItems.forEach { tool ->
                                                        FilterRowItem(
                                                            text = tool.name,
                                                            selected = selectedTypes[tool.id] == true,
                                                            onCheckedChange = { searchVm.toggleType(tool.id) }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                // Indicatori a punti (image_8d0828.png)
                                DotsIndicator(
                                    totalDots = (tools.distinctBy { it.name }.size / 6) + 1,
                                    visibleDot = listState.firstVisibleItemIndex
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // MAPPA CON PIN PERSONALIZZATI (image_5aa6ab.png)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(R.drawable.placeholder_map),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                lockers.forEachIndexed { index, locker ->
                                    // Utilizzo dell'asset 'pin' personalizzato
                                    Image(
                                        painter = painterResource(id = R.drawable.pin),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(35.dp)
                                            .offset((50 + index * 40).dp, (80 + index * 20).dp)
                                            .clickable {
                                                navController.navigate(NavRoutes.SchedaDistributore.createRoute(locker.id))
                                            }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(120.dp))
                    }
                }
            }

            // --- SWITCHER FLOTTANTE BASSO (image_5aa6ab.png) ---
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp).height(48.dp).width(110.dp),
                shape = RoundedCornerShape(50),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if(selectedTab == 0) BluePrimary.copy(alpha = 0.1f) else Color.Transparent).clickable { selectedTab = 0 }, contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Vista Lista",
                            tint = if(selectedTab == 0) BluePrimary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if(selectedTab == 1) BluePrimary.copy(alpha = 0.1f) else Color.Transparent).clickable { selectedTab = 1 }, contentAlignment = Alignment.Center) {
                        Icon(painterResource(id = R.drawable.pin), null, tint = if(selectedTab == 1) BluePrimary else Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRowItem(text: String, selected: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(140.dp).clickable { onCheckedChange(!selected) }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = Color.DarkGray
        )
        Checkbox(
            checked = selected,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Gray,
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            ),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun Switcher(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Invertiti: ora Distributore è a sinistra (index 0) e Attrezzo a destra (index 1)
        SwitcherButton(
            text = "Distributore",
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f)
        )
        SwitcherButton(
            text = "Attrezzo",
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MapListSwitcher(selectedMode: Int, onModeSelected: (Int) -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color(0xFFF5F5F5),
        modifier = Modifier.height(40.dp).width(120.dp) // Rimosso .align qui
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if(selectedMode == 0) BluePrimary else Color.Transparent).clickable { onModeSelected(0) }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Search, contentDescription = null, tint = if(selectedMode == 0) Color.White else Color.Gray, modifier = Modifier.size(20.dp))
            }
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if(selectedMode == 1) BluePrimary else Color.Transparent).clickable { onModeSelected(1) }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = if(selectedMode == 1) Color.White else Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SwitcherButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) YellowPrimary else Color(0xFFF2F2F2),
            contentColor = if (selected) Color.Black else Color.Gray
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
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
fun ToolFilterBox(text: String, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) YellowPrimary else Color.White
    val borderColor = if (selected) YellowPrimary else Color(0xFFE0E0E0)
    val contentColor = if (selected) Color.Black else Color.DarkGray

    Surface(
        modifier = Modifier
            .size(width = 100.dp, height = 110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (selected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF454545),
                        checkmarkColor = Color.White,
                        uncheckedColor = Color.Gray
                    ),
                    modifier = Modifier.size(20.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = if (selected) Color.Black else BluePrimary.copy(alpha = 0.7f)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}