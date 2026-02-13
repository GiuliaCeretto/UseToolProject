package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*

// SearchViewModel.kt
class SearchViewModel(private val inventoryRepository: InventoryRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val resultsTools: StateFlow<List<ToolDTO>> = combine(_query, inventoryRepository.tools) { q, list ->
        if (q.isBlank()) emptyList() else list.filter { it.name.contains(q, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setQuery(q: String) { _query.value = q }
}