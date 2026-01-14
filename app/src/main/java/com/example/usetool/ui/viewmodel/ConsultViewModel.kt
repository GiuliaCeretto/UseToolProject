package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.network.FirebaseDao
import com.example.usetool.data.network.FirebaseService
import com.example.usetool.data.repository.UseToolRepository
import com.example.usetool.data.dto.ExpertDTO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ConsultViewModel : ViewModel() {
    private val dao = FirebaseDao()
    private val service = FirebaseService(dao)
    private val repository = UseToolRepository(service)

    // Osserva direttamente gli esperti dal repository
    val experts: StateFlow<List<ExpertDTO>> = repository.experts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}