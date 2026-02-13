package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.LockerDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.repository.InventoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.math.*

class UseToolViewModel(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    // Posizione fittizia dell'utente (es. Centro Torino)
    private val userLat = 45.0703
    private val userLon = 7.6869

    // Osservazione dei dati reali dal Repository
    val topTools: StateFlow<List<ToolDTO>> = inventoryRepository.tools
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lockers: StateFlow<List<LockerDTO>> = inventoryRepository.lockers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Osservazione degli slot per verificare la disponibilità reale
    private val slots = inventoryRepository.slots
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- FUNZIONI ORIGINALI ---

    fun findToolById(id: String): ToolDTO? {
        return topTools.value.find { it.id == id }
    }

    fun findLockerById(id: String): LockerDTO? {
        return lockers.value.find { it.id == id }
    }

    fun getDistanceForTool(toolId: String): Double? {
        // 1. Trova gli ID dei locker che contengono questo specifico tool con stato "DISPONIBILE"
        val lockerIdsConTool = slots.value
            .filter { it.toolId == toolId && it.status == "DISPONIBILE" }
            .map { it.lockerId }
            .toSet()

        // 2. Calcola le distanze usando i campi 'lat' e 'lon' del tuo LockerDTO
        // Mappiamo a Double per risolvere l'ambiguità di overload di minOrNull
        val distances = lockers.value
            .filter { lockerIdsConTool.contains(it.id) }
            .map { calculateDistance(userLat, userLon, it.lat, it.lon) }

        return if (distances.isNotEmpty()) distances.minOrNull() else null
    }
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Raggio medio terrestre in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}