package com.example.usetool.data.repository

import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class InventoryRepository(
    private val dataSource: DataSource,
    private val toolDao: ToolDao,
    private val slotDao: SlotDao,
    private val lockerDao: LockerDao
) {
    val allLockers: Flow<List<LockerEntity>> = lockerDao.getAll()
    val allTools: Flow<List<ToolEntity>> = toolDao.getAllTools()
    val allSlots: Flow<List<SlotEntity>> = slotDao.getAllSlots()

    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        slotDao.getSlotsByLocker(lockerId)

    suspend fun syncFromNetwork() {
        // Avvolgiamo ogni sincronizzazione per permettere il completamento parziale
        try {
            dataSource.observeTools().collectLatest { toolDao.insertTools(it.toEntityList()) }
        } catch (_: Exception) { /* Errore loggabile o gestito dal chiamante */ }

        try {
            dataSource.observeSlots().collectLatest { slotDao.insertSlots(it.toEntityList()) }
        } catch (_: Exception) { /* Prosegue con gli altri */ }

        try {
            dataSource.observeLockers().collectLatest { lockerDao.insertAll(it.toEntityList()) }
        } catch (e: Exception) {
            // Rilanciamo l'eccezione se vogliamo che il ViewModel sappia del fallimento totale/parziale
            throw e
        }
    }

    suspend fun clearCache() {
        toolDao.clearAll()
        slotDao.clearAll()
    }
}