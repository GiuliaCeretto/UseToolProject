package com.example.usetool.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.example.usetool.R
import com.example.usetool.component.*
import com.example.usetool.navigation.*
import com.example.usetool.viewmodel.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import com.example.usetool.ui.theme.BluePrimary
import com.example.usetool.ui.theme.YellowMedium
import com.example.usetool.ui.theme.YellowPrimary


@Composable
fun HomeScreen(
    navController: NavController,
    vm: UseToolViewModel,
    cartVM: CartViewModel
) {
    val tools by vm.topTools.collectAsState()
    val lockers by vm.lockers.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // SALUTO
            Text(
                text = "Ciao, Mario 👋",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            // I TUOI NOLEGGI
            Text(
                text = "I tuoi noleggi",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp
                )
            )

            LazyRow {
                items(tools.filter { !it.available }) { tool ->
                    ToolCardSmall(
                        tool = tool,
                        onClick = {
                            navController.navigate(
                                NavRoutes.SchedaStrumento.createRoute(tool.id)
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // SWITCHER
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

            // CONTENUTO SWITCHER
            if (selectedTab == 0) {
                LazyRow {
                    items(tools.take(5)) { tool ->
                        ToolCardSmall(
                            tool = tool,
                            onClick = {
                                navController.navigate(
                                    NavRoutes.SchedaStrumento.createRoute(tool.id)
                                )
                            }
                        )
                    }
                }
            } else {
                LazyRow {
                    items(lockers.sortedBy { it.distanceKm }.take(5)) { locker ->
                        LockerCardSmall(
                            locker = locker,
                            onClick = {
                                navController.navigate(
                                    NavRoutes.SchedaDistributore.createRoute(locker.id)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // MAPPA
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
                        Column(
                            modifier = Modifier
                                .offset(
                                    x = (40 + index * 90).dp,
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
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 14.sp,
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

    }
}


