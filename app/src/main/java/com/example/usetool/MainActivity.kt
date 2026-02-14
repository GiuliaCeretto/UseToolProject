package com.example.usetool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.usetool.data.DatabaseInitializer
import com.example.usetool.data.service.Injection
import com.example.usetool.navigation.AppNavGraph
import com.example.usetool.ui.component.MainScaffold
import com.example.usetool.ui.theme.UseToolTheme
import com.example.usetool.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    // Usiamo 'lazy' o inizializziamo dopo init per sicurezza,
    // ma la soluzione più pulita è chiamare init IMMEDIATAMENTE.

    private val useToolVM: UseToolViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val cartVM: CartViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private val expertVM: ExpertViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. PRIMA DI TUTTO: Inizializza Injection.
        // Deve essere la primissima riga per evitare crash nei ViewModel
        Injection.init(this)

        // 2. Logica Firebase
        checkFirstRunAndInit()

        super.onCreate(savedInstanceState)

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
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }

    private fun checkFirstRunAndInit() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("is_first_run", true)

        if (isFirstRun) {
            DatabaseInitializer.runFullSetup(this)
            // Segnamo come fatto solo se non ci sono crash
            prefs.edit().putBoolean("is_first_run", false).apply()
        }
    }
}