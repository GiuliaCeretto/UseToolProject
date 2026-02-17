package com.example.usetool.ui.component

import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.usetool.R
import com.example.usetool.navigation.NavRoutes
import com.example.usetool.ui.theme.Green1
import com.example.usetool.ui.theme.YellowPrimary

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: Int,
    val selectedIcon: Int
)

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem(
            "Home",
            NavRoutes.Home.route,
            R.drawable.home,
            R.drawable.home_selected
        ),
        BottomNavItem(
            "Cerca",
            NavRoutes.Search.route,
            R.drawable.search,
            R.drawable.search_selected
        ),
        BottomNavItem(
            "Collega",
            NavRoutes.Collegamento.route,
            R.drawable.collegamento,
            R.drawable.collegamento_selected
        ),
        BottomNavItem(
            "Consulenza",
            NavRoutes.Consulenza.route,
            R.drawable.consulenza,
            R.drawable.consulenza_selected
        ),
        BottomNavItem(
            "Profilo",
            NavRoutes.Profilo.route,
            R.drawable.profilo,
            R.drawable.profilo_selected
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->

            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(NavRoutes.Home.route) { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Image(
                        painter = painterResource(
                            id = if (selected) item.selectedIcon else item.icon
                        ),
                        contentDescription = item.title
                    )
                },
                label = {

                    val selectedColor = when (item.route) {
                        NavRoutes.Consulenza.route -> Green1
                        else -> YellowPrimary
                    }

                    Text(
                        text = item.title,
                        color = if (selected) selectedColor else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
