package com.example.usetool.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.usetool.navigation.NavRoutes

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, NavRoutes.Home.route),
        BottomNavItem("Cerca", Icons.Default.Search, NavRoutes.Ricerca.route),
        BottomNavItem("Collega", Icons.Default.Lock, NavRoutes.Collegamento.route),
        BottomNavItem("Consulenza", Icons.Default.Info, NavRoutes.Consulenza.route),
        BottomNavItem("Profilo", Icons.Default.Person, NavRoutes.Profilo.route)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(NavRoutes.Home.route)
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

