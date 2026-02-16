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
    // Flussi reattivi per osservare i dati direttamente dal database Room locale
    val allLockers: Flow<List<LockerEntity>> = lockerDao.getAll()
    val allTools: Flow<List<ToolEntity>> = toolDao.getAllTools()
    val allSlots: Flow<List<SlotEntity>> = slotDao.getAllSlots()

    // Espone il flusso degli slot contrassegnati come preferiti localmente
    val favoriteSlots: Flow<List<SlotEntity>> = slotDao.getFavoriteSlots()

    /**
     * Recupera gli slot associati a un distributore specifico.
     */
    fun getSlotsForLocker(lockerId: String): Flow<List<SlotEntity>> =
        slotDao.getSlotsByLocker(lockerId)

    /**
     * Gestisce il flag 'isFavorite' in Room senza coinvolgere il database remoto.
     */
    suspend fun toggleSlotFavorite(slotId: String, isFavorite: Boolean) {
        slotDao.toggleFavorite(slotId, isFavorite)
    }

    /**
     * Sincronizza l'inventario globale preservando i dati locali (Shield logic).
     * I dati salvati sul database locale vengono scritti per primi per garantire reattività.
     */
    suspend fun syncFromNetwork() = coroutineScope {
        // 1. Sync Strumenti (Catalogo generale)
        launch {
            dataSource.observeTools().collectLatest { tools ->
                if (tools.isNotEmpty()) {
                    toolDao.insertTools(tools.toToolEntityList())
                }
            }
        }

        // 2. Sync Slot con logica di protezione (Shield) per stato carrello e preferiti
        launch {
            dataSource.observeSlots().collectLatest { remoteSlots ->
                if (remoteSlots.isNotEmpty()) {
                    // Recuperiamo gli item attualmente nel carrello locale per proteggerne lo stato
                    val itemsInCart = cartDao.getAllCartItemsSnapshot()
                    val idsInCart = itemsInCart.map { it.slotId }.toSet()

                    // Recuperiamo gli slot locali per preservare le preferenze dell'utente
                    val localSlots = slotDao.getAllSlots().firstOrNull() ?: emptyList()
                    val favoriteIds = localSlots.filter { it.isFavorite }.map { it.id }.toSet()

                    val entities = remoteSlots.toSlotEntityList().map { slot ->
                        val inCart = idsInCart.contains(slot.id)
                        val isFav = favoriteIds.contains(slot.id)

                        slot.copy(
                            // Se lo slot è nel carrello locale, forziamo lo stato corretto ignorando il network
                            status = if (inCart) "IN_CARRELLO" else slot.status,
                            // Preserviamo sempre il flag preferito salvato localmente
                            isFavorite = isFav
                        )
                    }
                    // Salvataggio atomico degli slot aggiornati nel database Room locale
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

    /**
     * Pulisce le tabelle locali dell'inventario.
     */
    suspend fun clearCache() {
        toolDao.clearAll()
        slotDao.clearAll()
        lockerDao.clearAll()
    }
}