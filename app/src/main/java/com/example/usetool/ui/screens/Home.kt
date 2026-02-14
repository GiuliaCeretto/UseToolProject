package com.example.usetool.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.component.*
import com.example.usetool.ui.theme.*
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
    val tools by vm.topTools.collectAsState()
    val lockers by vm.lockers.collectAsState()
    val rentals by userVm.rentals.collectAsState()
    val profile by userVm.userProfile.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Raccoglie errori da entrambi i ViewModel
    LaunchedEffect(vm.errorMessage, userVm.errorMessage) {
        merge(vm.errorMessage, userVm.errorMessage).collectLatest { message ->
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Ciao, ${profile?.nome ?: "Mario"} 👋",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            if (rentals.any { it.statoNoleggio == "ATTIVO" }) {
                Text(
                    text = "I tuoi noleggi attivi",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rentals.filter { it.statoNoleggio == "ATTIVO" }) { rental ->
                        val tool = tools.find { it.id == rental.toolId }
                        tool?.let {
                            ToolCardSmall(
                                tool = it,
                                onClick = {
                                    navController.navigate(NavRoutes.SchedaStrumento.createRoute(it.id))
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HomeSwitcher(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            if (selectedTab == 0) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tools.take(5)) { tool ->
                        ToolCardSmall(
                            tool = tool,
                            onClick = {
                                navController.navigate(NavRoutes.SchedaStrumento.createRoute(tool.id))
                            }
                        )
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(lockers.take(5)) { locker ->
                        LockerCardSmall(
                            locker = locker,
                            // Calcolo distanza reale dal ViewModel
                            distanceKm = vm.getDistanceForTool(tools.firstOrNull()?.id ?: ""),
                            onClick = {
                                navController.navigate(NavRoutes.SchedaDistributore.createRoute(locker.id))
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Trova sulla mappa",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
            )

            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.placeholder_map),
                        contentDescription = "Mappa",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    lockers.forEachIndexed { index, locker ->
                        val xPos = (40 + (locker.lon % 1.0) * 1000).dp
                        val yPos = (60 + (locker.lat % 1.0) * 1000).dp

                        Column(
                            modifier = Modifier
                                .offset(x = xPos.coerceIn(0.dp, 280.dp), y = yPos.coerceIn(0.dp, 150.dp))
                                .clickable {
                                    navController.navigate(NavRoutes.SchedaDistributore.createRoute(locker.id))
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

@Composable
fun HomeSwitcher(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp,
        color = YellowMedium
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwitcherItem(
                text = "Popolari",
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            SwitcherItem(
                text = "Vicini a te",
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
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
        color = if (selected) YellowPrimary else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else Color.DarkGray,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}