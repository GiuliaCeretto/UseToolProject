package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.data.repository.InventoryRepository
import kotlinx.coroutines.flow.*

// AGGIUNTO 'private val' per rendere il parametro una proprietà della classe
class SearchViewModel(private val inventoryRepository: InventoryRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    /**
     * Utilizzo 'by lazy' per l'inizializzazione pigra vera e propria.
     * Il Flow viene creato e la computazione 'combine' parte solo quando
     * la UI accede per la prima volta a 'resultsTools'.
     */
    val resultsTools: StateFlow<List<ToolEntity>> by lazy {
        combine(
            _query,
            inventoryRepository.allTools
        ) { q, list ->
            if (q.isBlank()) {
                emptyList()
            } else {
                list.filter { it.name.contains(q, ignoreCase = true) }
            }
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