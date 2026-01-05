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
                AppNavGraph(
                    useToolViewModel = useToolVM,
                    searchViewModel = searchVM,
                    cartViewModel = cartVM,
                    userViewModel = userVM,
                    linkingViewModel = linkingVM,
                    //consultViewModel = consultVM
                )
            }
        }
    }
}


