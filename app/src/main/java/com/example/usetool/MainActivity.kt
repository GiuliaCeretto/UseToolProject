package com.example.usetool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.usetool.component.MainScaffold
import com.example.usetool.navigation.AppNavGraph
import com.example.usetool.ui.theme.UseToolTheme
import com.example.usetool.ui.viewmodel.CartViewModel
import com.example.usetool.ui.viewmodel.LinkingViewModel
import com.example.usetool.ui.viewmodel.SearchViewModel
import com.example.usetool.ui.viewmodel.UseToolViewModel
import com.example.usetool.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private val useToolVM: UseToolViewModel by viewModels()
    private val searchVM: SearchViewModel by viewModels()
    private val cartVM: CartViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private val linkingVM: LinkingViewModel by viewModels()
    //private val consultVM: ConsultViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
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
                        linkingViewModel = linkingVM,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}


