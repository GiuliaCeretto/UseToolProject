package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.repository.ExpertRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpertViewModel(private val repository: ExpertRepository) : ViewModel() {

    // Il flusso parte al primo ascoltatore e rimane attivo finché il ViewModel esiste
    val experts: StateFlow<List<com.example.usetool.data.dao.ExpertEntity>> = repository.experts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    init {
        refreshExperts()
    }

    fun refreshExperts() {
        viewModelScope.launch {
            try {
                repository.syncExperts()
            } catch (_: Exception) {
                // L'underscore ignora l'eccezione senza generare warning
            }
        }
    }
}