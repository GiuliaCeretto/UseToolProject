package com.example.usetool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.usetool.navigation.NavGraph
import com.example.usetool.ui.theme.UseToolTheme
import com.example.usetool.viewModel.UseToolViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: UseToolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UseToolTheme {
                NavGraph(viewModel = viewModel)
            }
        }
    }
}
