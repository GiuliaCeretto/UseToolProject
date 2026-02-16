package com.example.usetool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.usetool.data.service.Injection
import com.example.usetool.navigation.AppNavGraph
import com.example.usetool.ui.component.MainScaffold
import com.example.usetool.ui.theme.UseToolTheme
import com.example.usetool.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    private val useToolVM: UseToolViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val cartVM: CartViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private val expertVM: ExpertViewModel by viewModels()
    private val linkingVM: LinkingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Inizializzazione delle dipendenze (Room, Firebase, etc.)
        enableEdgeToEdge()
        Injection.init(this)
        super.onCreate(savedInstanceState)

        // 2. TRIGGER DI SINCRONIZZAZIONE (Opzionale qui)
        // Invece di caricare i JSON, chiediamo ai ViewModel di aggiornare i dati da Firebase

        setContent {
            UseToolTheme {
                val navController = rememberNavController()

                MainScaffold(navController) { padding ->
                    AppNavGraph(
                        navController = navController,
                        useToolViewModel = useToolVM,
                        searchViewModel = searchVM,
                        cartViewModel = cartVM,
                        userViewModel = userVM,
                        expertViewModel = expertVM,
                        linkingViewModel = linkingVM,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }


}