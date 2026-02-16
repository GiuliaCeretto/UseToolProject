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
import com.example.usetool.R
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.*
import com.example.usetool.ui.viewmodel.UserViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge

@Composable
fun HomeScreen(
    navController: NavController,
    vm: UseToolViewModel,
    userVm: UserViewModel
) {
    val tools by vm.topTools.collectAsStateWithLifecycle()
    val lockers by vm.lockers.collectAsStateWithLifecycle()
    // Recuperiamo la lista reale dei preferiti dal ViewModel
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
                    0 -> ToolRow(tools, navController) // Popolari
                    1 -> {
                        // LOGICA AGGIORNATA: Se la lista è vuota mostra il placeholder,
                        // altrimenti mostra i preferiti reali
                        if (favoriteTools.isEmpty()) {
                            FavoriteToolsSection()
                        } else {
                            ToolRow(favoriteTools, navController)
                        }
                    }
                    2 -> LockerRow(lockers, vm, navController) // Vicini a te
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
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(R.drawable.placeholder_map),
                                contentDescription = "Mappa",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            val userPos = Offset(350f, 750f)
                            val lockerPos = Offset(650f, 450f)

                            Image(
                                painter = painterResource(id = R.drawable.pin),
                                contentDescription = "La tua posizione",
                                modifier = Modifier
                                    .size(32.dp)
                                    .offset(
                                        x = with(density) { userPos.x.toDp() - 16.dp },
                                        y = with(density) { userPos.y.toDp() - 32.dp }
                                    )
                            )

                            lockers.forEach { locker ->
                                Image(
                                    painter = painterResource(id = R.drawable.pin),
                                    contentDescription = locker.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .offset(
                                            x = with(density) { lockerPos.x.toDp() - 20.dp },
                                            y = with(density) { lockerPos.y.toDp() - 40.dp }
                                        )
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
        horizontalArrangement = Arrangement.spacedBy(12.dp), // Spaziatura migliorata per scannabilità
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(tools.take(5)) { tool ->
            ToolCardSmall(
                tool = tool,
                onClick = {
                    navController.navigate(NavRoutes.SchedaStrumento.createRoute(tool.id))
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
                address = locker.address, // Passiamo la via
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
    // Nota: In un'app reale filtreresti i 'tools' in base a un flag isFavorite o una lista di ID.
    // Qui simuliamo una lista vuota per mostrare il messaggio richiesto.

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icona stellina per i preferiti
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

