package com.example.usetool.component

import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.YellowPrimary

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem("Home", NavRoutes.Home.route, androidx.compose.material.icons.Icons.Default.Home),
        BottomNavItem("Cerca", NavRoutes.Search.route, androidx.compose.material.icons.Icons.Default.Search),
        BottomNavItem("Collega", NavRoutes.Collegamento.route, androidx.compose.material.icons.Icons.Default.Add),
        BottomNavItem("Consulenza", NavRoutes.Consulenza.route, androidx.compose.material.icons.Icons.Default.Phone),
        BottomNavItem("Profilo", NavRoutes.Profilo.route, androidx.compose.material.icons.Icons.Default.AccountCircle)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // Naviga solo se non sei già sullo stesso screen
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(NavRoutes.Home.route) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = YellowPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                )
            )
        }
    }
}



