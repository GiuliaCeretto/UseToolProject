package com.example.usetool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.usetool.data.service.Injection
import com.example.usetool.navigation.AppNavGraph
import com.example.usetool.ui.component.MainScaffold
import com.example.usetool.ui.theme.UseToolTheme
import com.example.usetool.ui.viewmodel.*

class MainActivity : ComponentActivity() {

    // --- VIEWMODEL CON COSTRUTTORE MANUALE (Richiedono Factory) ---

    private val arduinoVM: ArduinoViewModel by viewModels {
        factory { ArduinoViewModel(Injection.provideArduinoRepository()) }
    }

    private val cartVM: CartViewModel by viewModels {
        factory { CartViewModel(Injection.provideCartRepository()) }
    }

    private val orderVM: OrderViewModel by viewModels {
        factory { OrderViewModel(Injection.provideOrderRepository()) }
    }

    // --- VIEWMODEL SEMPLICI (Se non hanno parametri nel costruttore) ---

    private val useToolVM: UseToolViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private val expertVM: ExpertViewModel by viewModels()
    private val linkingVM: LinkingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inizializzazione cruciale delle dipendenze (Room + Firebase)
        Injection.init(this)

        enableEdgeToEdge()

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
                        orderViewModel = orderVM,
                        arduinoViewModel = arduinoVM, // Ora inizializzato correttamente
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }

    /**
     * Helper per creare velocemente le Factory dei ViewModel
     */
    private inline fun <reified T : ViewModel> factory(crossinline provider: () -> T) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return provider() as T
            }
        }
}