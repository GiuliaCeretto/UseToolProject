package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.network.FirebaseDao
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.repository.UseToolRepository
import kotlinx.coroutines.flow.*

class SearchViewModel : ViewModel() {
    private val repository = UseToolRepository(DataSource(FirebaseDao()))

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Risultati filtrati dinamicamente
    val resultsTools: StateFlow<List<ToolDTO>> = combine(_query, repository.tools) { q, list ->
        if (q.isBlank()) emptyList() else list.filter { it.name.contains(q, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setQuery(q: String) { _query.value = q }
}