package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.ExpertEntity
import com.example.usetool.data.repository.ExpertRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExpertViewModel(
    private val expertRepository: ExpertRepository = Injection.provideExpertRepository()
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    val experts: StateFlow<List<ExpertEntity>> = expertRepository.experts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshExperts()
    }

    fun refreshExperts() {
        viewModelScope.launch {
            try {
                expertRepository.syncExperts()
            } catch (_: Exception) {
                _errorMessage.emit("Errore nel caricamento esperti")
            }
        }
    }
}