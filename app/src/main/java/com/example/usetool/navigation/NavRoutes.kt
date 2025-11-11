package com.example.usetool.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Distributore : NavRoutes("distributore/{id}") {
        fun createRoute(id: String) = "distributore/$id"
    }
    object Strumento : NavRoutes("strumento/{id}") {
        fun createRoute(id: String) = "strumento/$id"
    }
    object Consulenza : NavRoutes("consulenza")
    object MieiStrumenti : NavRoutes("mieiStrumenti")
    object InizioNoleggio : NavRoutes("inizioNoleggio/{toolId}") {
        fun createRoute(toolId: String) = "inizioNoleggio/$toolId"
    }
    object Ricerca : NavRoutes("ricerca")
    object Profilo : NavRoutes("profilo")
}
