package com.example.usetool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.usetool.navigation.AppNavGraph
import com.example.usetool.ui.theme.UseToolTheme
import com.example.usetool.viewmodel.*

class MainActivity : ComponentActivity() {

    // ViewModels principali (puoi aggiungerne altri come necessario)
    private val useToolVM: UseToolViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val cartVM: CarrelloViewModel by viewModels()
    private val userVM: ProfiloViewModel by viewModels()
    private val collegamentoVM: CollegamentoViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UseToolTheme {
                AppNavGraph(
                    UseToolViewModel = useToolVM,
                    SearchViewModel = searchVM,
                    CartViewModel = cartVM,
                    UserViewModel = userVM,
                    CollegamentoViewModel = collegamentoVM
                )
            }
        }
    }
}


