package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*

class SearchViewModel(private val inventoryRepository: InventoryRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // CORRETTO: Gestisce solo Entity e logica di presentazione
    val resultsTools: StateFlow<List<ToolEntity>> by lazy {
        combine(
            _query,
            inventoryRepository.allTools
        ) { q, list ->
            if (q.isBlank()) emptyList()
            else list.filter { it.name.contains(q, ignoreCase = true) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    fun setQuery(q: String) {
        _query.value = q
    }
}