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
    // Esposizione dei dati da Room
    val allLockers: Flow<List<LockerEntity>> = lockerDao.getAll()
    val allTools: Flow<List<ToolEntity>> = toolDao.getAllTools()
    val allSlots: Flow<List<SlotEntity>> = slotDao.getAllSlots()

    // Metodo mancante per recuperare gli slot di un locker specifico
    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        slotDao.getSlotsByLocker(lockerId)

    /**
     * Sincronizza l'intero inventario dal Network a Room.
     * Implementa lo "Scudo" per non sovrascrivere gli articoli nel carrello.
     */
    suspend fun syncFromNetwork() = coroutineScope {
        // 1. Sync Strumenti
        launch {
            dataSource.observeTools().collectLatest { tools ->
                if (tools.isNotEmpty()) toolDao.insertTools(tools.toToolEntityList())
            }
        }

        // 2. Sync Slot con SCUDO ANTI-SOVRASCRITTURA
        launch {
            dataSource.observeSlots().collectLatest { remoteSlots ->
                if (remoteSlots.isNotEmpty()) {
                    // Recuperiamo gli ID degli slot attualmente nel carrello locale
                    val itemsInCart = cartDao.getAllCartItemsSnapshot()
                    val idsInCart = itemsInCart.map { it.slotId }.toSet()

                    val entities = remoteSlots.toSlotEntityList().map { slot ->
                        // Se lo slot è nel carrello, forziamo lo stato locale "IN_CARRELLO"
                        // ignorando il "DISPONIBILE" che arriva dal server (non ancora aggiornato)
                        if (idsInCart.contains(slot.id)) {
                            slot.copy(status = "IN_CARRELLO")
                        } else {
                            slot
                        }
                    }
                    slotDao.insertSlots(entities)
                }
            }
        }

        // 3. Sync Locker
        launch {
            dataSource.observeLockers().collectLatest { lockers ->
                if (lockers.isNotEmpty()) lockerDao.insertAll(lockers.toLockerEntityList())
            }
        }
    }

    suspend fun clearCache() {
        toolDao.clearAll()
        slotDao.clearAll()
        lockerDao.clearAll()
    }
}