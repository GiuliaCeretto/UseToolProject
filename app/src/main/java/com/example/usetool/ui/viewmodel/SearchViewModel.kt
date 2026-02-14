package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.data.repository.InventoryRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.*

class SearchViewModel(
    private val inventoryRepository: InventoryRepository = Injection.provideInventoryRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _maxDistance = MutableStateFlow(5f)
    val maxDistance: StateFlow<Float> = _maxDistance

    private val _selectedTypes = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val selectedTypes: StateFlow<Map<String, Boolean>> = _selectedTypes

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    val filteredTools: StateFlow<List<ToolEntity>> = combine(
        _query,
        _maxDistance,
        inventoryRepository.allTools
    ) { q, _, tools ->
        try {
            if (q.isBlank()) tools
            else tools.filter { it.name.contains(q, ignoreCase = true) }
        } catch (_: Exception) {
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setQuery(q: String) { _query.value = q }
    fun setMaxDistance(d: Float) { _maxDistance.value = d }
    fun toggleType(toolId: String) {
        val current = _selectedTypes.value.toMutableMap()
        current[toolId] = !(current[toolId] ?: false)
        _selectedTypes.value = current
    }
}