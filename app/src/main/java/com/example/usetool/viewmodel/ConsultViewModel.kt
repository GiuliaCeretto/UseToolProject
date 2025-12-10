package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConsultViewModel : ViewModel() {
    private val _experts = MutableStateFlow<List<Expert>>(listOf(
        Expert("e1","Mario Rossi","Idraulico",30.0),
        Expert("e2","Luca Bianchi","Elettricista",25.0)
    ))
    val experts: StateFlow<List<Expert>> = _experts
}
