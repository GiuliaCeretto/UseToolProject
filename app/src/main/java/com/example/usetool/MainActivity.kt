package com.example.usetool

import android.os.Bundle
import android.util.Log
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

    // ViewModel inizializzati correttamente tramite 'by viewModels()'
    private val useToolVM: UseToolViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val cartVM: CartViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()

    private val expertVM: ExpertViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Inizializza Injection (contesto per DB locale)
        Injection.init(applicationContext)

        // 2. Logica per il caricamento dati Firebase
        checkFirstRunAndInit()

        super.onCreate(savedInstanceState)

        setContent {
            UseToolTheme {
                val navController = rememberNavController()

                MainScaffold(navController) { padding ->
                    // CORREZIONE 2: Allineamento parametri con AppNavGraph.kt
                    // Rimosso 'linkingViewModel' e aggiunto 'expertViewModel'
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
            // CORREZIONE 3: Se runFullSetup accetta solo il Context,
            // gestiamo il flag delle preferenze subito dopo o verifichiamo la firma del metodo.
            try {
                DatabaseInitializer.runFullSetup(this)
                // Se il metodo non supporta una callback (lambda), settiamo il flag qui
                prefs.edit().putBoolean("is_first_run", false).apply()
                Log.d("APP_START", "Configurazione iniziale avviata")
            } catch (e: Exception) {
                Log.e("APP_START", "Errore durante l'inizializzazione: ${e.message}")
            }
        }
    }
}