package com.example.usetool.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.usetool.navigation.NavRoutes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

data class BottomNavItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem("Home", NavRoutes.Home.route, Icons.Default.Home),
        BottomNavItem("Cerca", NavRoutes.Search.route, Icons.Default.Search),
        BottomNavItem("Collega", NavRoutes.Collegamento.route, Icons.Default.Add),
        BottomNavItem("Consulenza", NavRoutes.Consulenza.route, Icons.Default.Phone),
        BottomNavItem("Profilo", NavRoutes.Profilo.route, Icons.Default.AccountCircle)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(NavRoutes.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}


