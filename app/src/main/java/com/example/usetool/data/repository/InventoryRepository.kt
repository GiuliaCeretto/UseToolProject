package com.example.usetool.data.repository

import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventoryRepository(
    private val dataSource: DataSource,
    private val toolDao: ToolDao,
    private val slotDao: SlotDao,
    private val lockerDao: LockerDao,
    private val cartDao: CartDao
) {
    // --- FLUSSI REATTIVI ---
    val allLockers: Flow<List<LockerEntity>> = lockerDao.getAll()

    val allTools: Flow<List<ToolEntity>> = toolDao.getAllTools()
    val allSlots: Flow<List<SlotEntity>> = slotDao.getAllSlots()

    // --- METODI DI ACCESSO AI DATI ---
    suspend fun getAllLockersSnapshot(): List<LockerEntity> {
        return lockerDao.getAll().first()
    }

    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        slotDao.getSlotsByLocker(lockerId)

    suspend fun toggleSlotFavorite(slotId: String, isFavorite: Boolean) {
        slotDao.toggleFavorite(slotId, isFavorite)
    }

    // --- LOGICA DI SINCRONIZZAZIONE ---
    suspend fun syncFromNetwork() = coroutineScope {
        // 1. Sync Strumenti
        launch {
            dataSource.observeTools().collectLatest { tools ->
                if (tools.isNotEmpty()) {
                    toolDao.insertTools(tools.toToolEntityList())
                }
            }
        }

        // 2. Sync Slot con logica di protezione (Shield logic) per il carrello e i preferiti
        launch {
            dataSource.observeSlots().collectLatest { remoteSlots ->
                if (remoteSlots.isNotEmpty()) {
                    val itemsInCart = cartDao.getAllCartItemsSnapshot()
                    val idsInCart = itemsInCart.map { it.slotId }.toSet()

                    val localSlots = slotDao.getAllSlots().firstOrNull() ?: emptyList()
                    val favoriteIds = localSlots.filter { it.isFavorite }.map { it.id }.toSet()

                    val entities = remoteSlots.toSlotEntityList().map { slot ->
                        val inCart = idsInCart.contains(slot.id)
                        val isFav = favoriteIds.contains(slot.id)

                        slot.copy(
                            status = if (inCart) "IN_CARRELLO" else slot.status,
                            isFavorite = isFav
                        )
                    }
                    slotDao.insertSlots(entities)
                }
            }
        }

        // 3. Sync Distributori (Locker)
        launch {
            dataSource.observeLockers().collectLatest { lockers ->
                if (lockers.isNotEmpty()) {
                    lockerDao.insertAll(lockers.toLockerEntityList())
                }
            }
        }
    }

    //Pulisce la cache locale di Room per tutti i dati dell'inventario.
    suspend fun clearCache() {
        toolDao.clearAll()
        slotDao.clearAll()
        lockerDao.clearAll()
    }
}