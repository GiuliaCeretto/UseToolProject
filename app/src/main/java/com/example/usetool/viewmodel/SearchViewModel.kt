package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.Tool
import com.example.usetool.model.Locker

class SearchViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _resultsTools = MutableStateFlow<List<Tool>>(emptyList())
    val resultsTools: StateFlow<List<Tool>> = _resultsTools

    private val _resultsDistributors = MutableStateFlow<List<Locker>>(emptyList())
    val resultsDistributors: StateFlow<List<Locker>> = _resultsDistributors

    fun setQuery(q: String) {
        _query.value = q
        // mock filtering logic to fill results
    }

    fun setResults(tools: List<Tool>, distributors: List<Locker>) {
        _resultsTools.value = tools
        _resultsDistributors.value = distributors
    }
}
