package com.example.usetool.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Search : NavRoutes("search")
    object Collegamento : NavRoutes("collegamento")
    object Consulenza : NavRoutes("consulenza")
    object Profilo : NavRoutes("profilo")
    object Login : NavRoutes("login")

    object SchedaDistributore : NavRoutes("distributore/{id}") {
        fun createRoute(id: String) = "distributore/$id"
    }

    object SchedaStrumento : NavRoutes("strumento/{id}") {
        fun createRoute(id: String) = "strumento/$id"
    }

    object Carrello : NavRoutes("carrello")
    object Pagamento : NavRoutes("pagamento")

    object SchedaConsulente : NavRoutes("consulente/{id}") {
        fun createRoute(id: String) = "consulente/$id"
    }
}
