package com.example.usetool.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Search : NavRoutes("search")
    object Collegamento : NavRoutes("collegamento")
    object Consulenza : NavRoutes("consulenza")
    object Profilo : NavRoutes("profilo")

    object SchedaDistributore : NavRoutes("distributore/{id}") {
        fun createRoute(id: String) = "distributore/$id"
    }

    object SchedaStrumento : NavRoutes("strumento/{id}") {
        fun createRoute(id: String) = "strumento/$id"
    }

    object SchedaStrumentoFiltrata : NavRoutes("strumento_filtrata/{id}") {
        fun createRoute(id: String) = "strumento_filtrata/$id"
    }

    object SchedaStrumentoNoleggiata : NavRoutes("strumento_noleggiato/{id}") {
        fun createRoute(id: String) = "strumento_noleggiato/$id"
    }

    object Carrello : NavRoutes("carrello")
    object Pagamento : NavRoutes("pagamento")
}
