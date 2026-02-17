package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.ToolCardSmall
import com.example.usetool.ui.component.LockerCardSmall
import com.example.usetool.ui.theme.YellowPrimary
import com.example.usetool.ui.viewmodel.UserViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import kotlin.random.Random

@Composable
fun HomeScreen(
    navController: NavController,
    vm: UseToolViewModel,
    userVm: UserViewModel
) {
    val tools by vm.topTools.collectAsStateWithLifecycle()
    val lockers by vm.lockers.collectAsStateWithLifecycle()
    val favoriteTools by vm.favoriteTools.collectAsStateWithLifecycle()

    val density = LocalDensity.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(vm.errorMessage, userVm.errorMessage) {
        merge(vm.errorMessage, userVm.errorMessage).collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HomeSwitcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            item {
                when (selectedTab) {
                    0 -> ToolRow(tools, navController)
                    1 -> {
                        if (favoriteTools.isEmpty()) {
                            FavoriteToolsSection()
                        } else {
                            ToolRow(favoriteTools, navController)
                        }
                    }
                    2 -> LockerRow(lockers, vm, navController) // Lista distributori orizzontale
                }
            }

            // Mappa visibile solo se non siamo nella tab preferiti
            if (selectedTab != 1) {
                item {
                    Text(
                        text = "Trova sulla Mappa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Mappa placeholder
                            Image(
                                painter = painterResource(R.drawable.placeholder_map),
                                contentDescription = "Mappa",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Posizione utente al centro
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "La tua posizione",
                                    tint = YellowPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            // Pin distributori sulla mappa
                            lockers.forEachIndexed { index, locker ->
                                LockerPinHome(
                                    locker = locker,
                                    index = index,
                                    totalLockers = lockers.size,
                                    onClick = {
                                        navController.navigate(
                                            NavRoutes.SchedaDistributore.createRoute(locker.id)
                                        )
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
fun LockerPinHome(
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
        val rowSpacingFactor = 0.7f
        val baseX = horizontalMargin + colIndex * ((0.7f - 2 * horizontalMargin) / (columns - 1))
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
                tint = Color(0xFF1976D2),
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
fun HomeSwitcher(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Popolari", "Preferiti", "Vicini a te").forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Button(
                onClick = { onTabSelected(index) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFFFFC107) else Color(0xFFF2F2F2)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                Text(
                    text = title,
                    color = if (isSelected) Color.Black else Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ToolRow(tools: List<ToolEntity>, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(tools.take(5)) { toolEntity ->
            ToolCardSmall(
                tool = toolEntity,
                onClick = {
                    navController.navigate(NavRoutes.SchedaStrumento.createRoute(toolEntity.id))
                }
            )
        }
    }
}

@Composable
fun LockerRow(lockers: List<LockerEntity>, vm: UseToolViewModel, navController: NavController) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(lockers.take(5)) { locker ->
            LockerCardSmall(
                locker = locker,
                address = locker.address,
                distanceKm = vm.getDistanceToLocker(locker),
                onClick = {
                    navController.navigate(NavRoutes.SchedaDistributore.createRoute(locker.id))
                }
            )
        }
    }
}

@Composable
fun FavoriteToolsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Nessun oggetto preferito",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
