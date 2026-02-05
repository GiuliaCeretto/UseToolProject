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
import com.example.usetool.component.AppTopBar
import com.example.usetool.component.BottomNavBar
import com.example.usetool.component.ToolCardSmall
import com.example.usetool.component.LockerCardSmall
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.viewmodel.CartViewModel
import com.example.usetool.viewmodel.UseToolViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun HomeScreen(
    navController: NavController,
    vm: UseToolViewModel,
    cartVM: CartViewModel
) {
    val tools by vm.topTools.collectAsState()
    val lockers by vm.lockers.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

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
        Text("I tuoi noleggi", style = MaterialTheme.typography.titleMedium)

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
        Text("Trova sulla mappa", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.placeholder_map),
                contentDescription = "Mappa",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
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
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwitcherItem("Popolari", selectedTab == 0) {
                onTabSelected(0)
            }
            SwitcherItem("Vicini a te", selectedTab == 1) {
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


