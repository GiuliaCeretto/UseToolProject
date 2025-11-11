package com.example.usetool.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.usetool.model.Tool

class UseToolViewModel : ViewModel() {

    val tools = mutableStateListOf(
        Tool("1", "Trapano", "Trapano a percussione", available = true),
        Tool("2", "Martello", "Martello universale", available = false)
    )

    var selectedTool: Tool? = null
}
