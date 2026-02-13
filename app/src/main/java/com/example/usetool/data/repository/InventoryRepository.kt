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
    private val lockerDao: LockerDao // Aggiunto per risolvere i warning di LockerDao
) {
    // RISOLTO: Utilizzo di getAll(), getAllTools() e getAllSlots()
    val allLockers: Flow<List<LockerEntity>> = lockerDao.getAll()
    val allTools: Flow<List<ToolEntity>> = toolDao.getAllTools()
    val allSlots: Flow<List<SlotEntity>> = slotDao.getAllSlots()

    // RISOLTO: Utilizzo di getSlotsByLocker()
    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        slotDao.getSlotsByLocker(lockerId)

    suspend fun syncFromNetwork() {
        dataSource.observeTools().collectLatest { toolDao.insertTools(it.toEntityList()) }
        dataSource.observeSlots().collectLatest { slotDao.insertSlots(it.toEntityList()) }
        dataSource.observeLockers().collectLatest { lockerDao.insertAll(it.toEntityList()) }
    }

    // RISOLTO: Utilizzo di clearAll() per Tool e Slot
    suspend fun clearCache() {
        toolDao.clearAll()
        slotDao.clearAll()
    }
}