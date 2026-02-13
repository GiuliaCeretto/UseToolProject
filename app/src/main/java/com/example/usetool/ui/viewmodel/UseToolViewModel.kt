package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.SlotEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.data.repository.InventoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class UseToolViewModel(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val userLat = 45.0703
    private val userLon = 7.6869

    /**
     * Utilizzo 'by lazy' per far sì che il Flow venga creato
     * solo quando la UI (o un altro metodo) vi accede per la prima volta.
     */
    val topTools: StateFlow<List<ToolEntity>> by lazy {
        inventoryRepository.allTools
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly, // Una volta creato, parte subito
                initialValue = emptyList()
            )
    }

    val lockers: StateFlow<List<LockerEntity>> by lazy {
        inventoryRepository.allLockers
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )
    }

    private val slots: StateFlow<List<SlotEntity>> by lazy {
        inventoryRepository.allSlots
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )
    }

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                inventoryRepository.syncFromNetwork()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- LOGICA DI BUSINESS ---

    fun getDistanceForTool(toolId: String): Double? {
        // Accedendo a slots.value e lockers.value qui,
        // i delegati 'lazy' attiveranno l'inizializzazione se non è già avvenuta.
        val currentSlots = slots.value
        val currentLockers = lockers.value

        val availableLockerIds = currentSlots
            .filter { it.toolId == toolId && it.status == "DISPONIBILE" }
            .map { it.lockerId }
            .toSet()

        return currentLockers
            .filter { availableLockerIds.contains(it.id) }
            .minOfOrNull { calculateDistance(userLat, userLon, it.lat, it.lon) }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        return r * (2 * atan2(sqrt(a), sqrt(1 - a)))
    }
}