package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.dao.SlotEntity
import com.example.usetool.data.dao.ToolEntity
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

    // Coordinate utente (potevano essere recuperate da un LocationService)
    private val userLat = 45.0703
    private val userLon = 7.6869

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // Liste principali osservate dal database Room
    val topTools = inventoryRepository.allTools
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val lockers = inventoryRepository.allLockers
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val slots = inventoryRepository.allSlots
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    /**
     * StateFlow per i Preferiti.
     * Reagisce istantaneamente quando toggleFavorite viene chiamato.
     */
    val favoriteTools: StateFlow<List<ToolEntity>> = combine(slots, topTools) { currentSlots, allTools ->
        val favoriteToolIds = currentSlots
            .filter { it.isFavorite }
            .map { it.toolId }
            .toSet()

        allTools.filter { favoriteToolIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * AGGIUNTO: StateFlow per gli strumenti effettivamente disponibili (quantità > 0).
     * Utile per filtrare la lista nella Home o nella ricerca.
     */
    val availableTools: StateFlow<List<ToolEntity>> = combine(slots, topTools) { currentSlots, allTools ->
        val availableToolIds = currentSlots
            .filter { it.status == "DISPONIBILE" && it.quantity > 0 }
            .map { it.toolId }
            .toSet()

        allTools.filter { availableToolIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                inventoryRepository.syncFromNetwork()
            } catch (e: Exception) {
                _errorMessage.emit("Errore di sincronizzazione: ${e.message}")
            }
        }
    }

    /**
     * Aggiorna lo stato dei preferiti sia in locale che (se previsto dal repository) su Firebase.
     */
    fun toggleFavorite(slotId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            inventoryRepository.toggleSlotFavorite(slotId, isFavorite)
        }
    }

    /**
     * Restituisce gli slot associati a un distributore specifico.
     */
    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        inventoryRepository.getSlotsForLocker(lockerId)

    /**
     * Calcola la distanza minima per trovare un determinato strumento.
     */
    fun getDistanceForTool(toolId: String): Double? {
        val currentSlots = slots.value
        val currentLockers = lockers.value

        val associatedLockerIds = currentSlots
            .filter { it.toolId == toolId && it.quantity > 0 } // Solo se disponibile
            .map { it.lockerId }
            .toSet()

        return currentLockers
            .filter { associatedLockerIds.contains(it.id) }
            .minOfOrNull { calculateDistance(userLat, userLon, it.lat, it.lon) }
    }

    fun getDistanceToLocker(locker: LockerEntity): Double {
        return calculateDistance(userLat, userLon, locker.lat, locker.lon)
    }

    /**
     * Formula di Haversine per il calcolo della distanza tra due coordinate.
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Raggio della terra in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}