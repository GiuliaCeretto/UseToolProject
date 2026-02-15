package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.SlotEntity
import com.example.usetool.data.repository.InventoryRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class UseToolViewModel(
    private val inventoryRepository: InventoryRepository = Injection.provideInventoryRepository()
) : ViewModel() {

    private val userLat = 45.0703
    private val userLon = 7.6869

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    val topTools = inventoryRepository.allTools.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val lockers = inventoryRepository.allLockers.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val slots = inventoryRepository.allSlots.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                inventoryRepository.syncFromNetwork()
            } catch (_: Exception) {
                _errorMessage.emit("Errore di sincronizzazione inventario")
            }
        }
    }

    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        inventoryRepository.getSlotsForLocker(lockerId)

    // In UseToolViewModel.kt
    fun getDistanceForTool(toolId: String): Double? {
        val currentSlots = slots.value
        val currentLockers = lockers.value

        val associatedLockerIds = currentSlots
            .filter { it.toolId == toolId }
            .map { it.lockerId }
            .toSet()

        return currentLockers
            .filter { associatedLockerIds.contains(it.id) }
            .minOfOrNull { calculateDistance(userLat, userLon, it.lat, it.lon) }
    }

    fun getDistanceToLocker(locker: LockerEntity): Double {
        return calculateDistance(userLat, userLon, locker.lat, locker.lon)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}