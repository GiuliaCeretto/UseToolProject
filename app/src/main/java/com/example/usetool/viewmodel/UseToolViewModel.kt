package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.Tool
import com.example.usetool.model.Locker

class UseToolViewModel : ViewModel() {

    private val _topTools = MutableStateFlow<List<Tool>>(emptyList())
    val topTools: StateFlow<List<Tool>> = _topTools

    private val _lockers = MutableStateFlow<List<Locker>>(emptyList())
    val lockers: StateFlow<List<Locker>> = _lockers

    init {
        loadMocks()
    }

    private fun loadMocks() {
        val t = listOf(
            Tool("1","Trapano","Trapano a percussione", null, true, 1.5),
            Tool("2","Martello","Martello universale", null, true, 0.5),
            Tool("3","Sega","Sega manuale", null, true, 0.8),
            Tool("4","Avvitatore","Avvitatore elettrico", null, true, 2.0),
            Tool("5","Livella","Livella a bolla", null, true, 0.6),
            Tool("6","Pialla","Pialla elettrica", null, false, 3.0)
        )
        _topTools.value = t.take(5)

        _lockers.value = listOf(
            Locker("d1","Ferramenta Centrale","Via A, 1", 45.0703, 7.6869, listOf("1","2","3")),
            Locker("d2","Bricolage Store","Via B, 2", 45.0710, 7.6900, listOf("2","4")),
            Locker("d3","Mini Store","Via C, 3",45.0680,7.6920, listOf("1","5"))
        )
    }

    fun findToolById(id: String): Tool? = (_topTools.value + _topTools.value).find { it.id == id } // naive
    fun findLockerById(id: String): Locker? = _lockers.value.find { it.id == id }
}