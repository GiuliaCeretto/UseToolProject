package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.LockerDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.network.FirebaseDao
import com.example.usetool.data.network.FirebaseService
import com.example.usetool.data.repository.UseToolRepository
import kotlinx.coroutines.flow.*

class SearchViewModel : ViewModel() {
    private val repository = UseToolRepository(FirebaseService(FirebaseDao()))
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val resultsTools: StateFlow<List<ToolDTO>> = combine(_query, repository.tools) { q, tools ->
        if (q.isBlank()) emptyList() else tools.filter { it.name.contains(q, true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val resultsDistributors: StateFlow<List<LockerDTO>> = combine(_query, repository.lockers) { q, lockers ->
        if (q.isBlank()) emptyList() else lockers.filter { it.name.contains(q, true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setQuery(q: String) { _query.value = q }
}