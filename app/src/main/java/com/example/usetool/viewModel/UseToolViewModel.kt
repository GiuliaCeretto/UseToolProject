package com.example.usetool.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.usetool.model.Tool

class UseToolViewModel : ViewModel() {

    val tools = mutableStateListOf(
        Tool("1", "Trapano", "Trapano a percussione", available = true),
        Tool("2", "Martello", "Martello universale", available = false),
        Tool("3", "Chiodi", "Chiodi chiodini", available = true),
        Tool("4", "Avvitatore", "Avvitatore velocissimo", available = true),
        Tool("5", "Viti", "Viti piccole", available = false),
        Tool("6", "Occhiali", "Occhiali prottettivi", available = true)
    )

    var selectedTool: Tool? = null
}
