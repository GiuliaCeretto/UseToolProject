package com.example.usetool.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Search : NavRoutes("search")
    object Collegamento : NavRoutes("collegamento")
    object Consulenza : NavRoutes("consulenza")
    object Profilo : NavRoutes("profilo")
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object Carrello : NavRoutes("carrello")

    object SchedaDistributore : NavRoutes("distributore/{id}") {
        fun createRoute(id: String) = "distributore/$id"
    }

    object SchedaStrumento : NavRoutes("strumento/{id}") {
        fun createRoute(id: String) = "strumento/$id"
    }

    object Pagamento : NavRoutes("pagamento/{lockerId}") {
        fun createRoute(lockerId: Int) = "pagamento/$lockerId"
    }

    object Linking : NavRoutes("linking/{lockerIds}") {
        fun createRoute(lockerIds: String) = "linking/$lockerIds"
    }

    object SchedaConsulente : NavRoutes("consulente/{id}") {
        fun createRoute(id: String) = "consulente/$id"
    }
}