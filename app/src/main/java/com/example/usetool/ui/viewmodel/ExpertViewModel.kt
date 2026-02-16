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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // Query per la ricerca locale degli esperti
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Esposizione degli esperti con logica di ricerca integrata
    val experts: StateFlow<List<ExpertEntity>> = combine(
        expertRepository.experts,
        _searchQuery
    ) { list, query ->
        if (query.isEmpty()) list
        else list.filter {
            it.firstName.contains(query, ignoreCase = true) ||
                    it.profession.contains(query, ignoreCase = true) ||
                    it.focus.contains(query, ignoreCase = true) // Ricerca anche nel nuovo campo focus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        refreshExperts()
    }

    fun setQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshExperts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                expertRepository.syncExperts()
            } catch (e: Exception) {
                _errorMessage.emit("Errore nel caricamento esperti: ${e.localizedMessage}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}