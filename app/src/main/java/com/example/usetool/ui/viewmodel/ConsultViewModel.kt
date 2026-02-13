package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.repository.ExpertRepository
import com.example.usetool.data.dto.ExpertDTO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ConsultViewModel(private val repository: ExpertRepository) : ViewModel() {
    // Espone il flusso degli esperti dal repository alla UI
    val experts: StateFlow<List<ExpertDTO>> = repository.experts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}