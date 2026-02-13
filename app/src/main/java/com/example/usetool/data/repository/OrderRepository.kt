package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull

class OrderRepository(
    private val dataSource: DataSource,
    private val purchaseDao: PurchaseDao,
    private val rentalDao: RentalDao,
    private val cartDao: CartDao,       // AGGIUNTO per recuperare i dettagli degli item
    private val toolDao: ToolDao,       // AGGIUNTO per recuperare i tipi di tool (acquisto/noleggio)
    private val cartRepository: CartRepository
) {
    val localPurchases: Flow<List<PurchaseEntity>> = purchaseDao.getAll()
    val localRentals: Flow<List<RentalEntity>> = rentalDao.getAllRentals()

    suspend fun syncUserOrders(userId: String) {
        dataSource.observePurchases(userId).collectLatest {
            purchaseDao.insertAll(it.toEntityList())
        }
        dataSource.observeRentals(userId).collectLatest {
            rentalDao.insertRentals(it.toEntityList())
        }
    }

    /**
     * CORRETTO: Ora accetta CartEntity e recupera internamente i dati necessari.
     */
    suspend fun processCheckout(userId: String, cart: CartEntity) {
        val updates = mutableMapOf<String, Any?>()
        val now = System.currentTimeMillis()

        // Recupera gli item del carrello e la lista dei tools dai DAO locali
        val items = cartDao.getItemsByCartId(cart.id).firstOrNull() ?: emptyList()
        val toolsList = toolDao.getAllTools().firstOrNull() ?: emptyList()

        items.forEach { item ->
            // Trova il tool corrispondente per conoscerne il tipo (acquisto o noleggio)
            val tool = toolsList.find { it.name == item.toolName } ?: return@forEach

            if (tool.type == "acquisto") {
                val pKey = "p_${now}_${item.slotId}"
                updates["purchases/$userId/$pKey"] = PurchaseDTO(
                    id = pKey,
                    toolName = tool.name,
                    prezzoPagato = tool.price,
                    dataAcquisto = now,
                    lockerId = "Locker_Default" // O recuperato da SlotEntity se aggiunto
                )
                updates["slots/${item.slotId}/status"] = "VUOTO"
            } else {
                val rKey = "r_${now}_${item.slotId}"
                updates["rentals/$userId/$rKey"] = RentalDTO(
                    id = rKey,
                    userId = userId,
                    toolId = tool.id,
                    toolName = tool.name,
                    slotId = item.slotId,
                    dataInizio = now,
                    statoNoleggio = "ATTIVO"
                )
                updates["slots/${item.slotId}/status"] = "NOLEGGIATO"
            }
        }

        // Resetta il carrello su Firebase
        updates["carts/$userId"] = CartDTO(userId = userId, status = "CONFERMATO")

        dataSource.updateMultipleNodes(updates)

        // Svuota Room localmente
        cartRepository.clearLocalCart(cart.id)
    }

    suspend fun clearOrderHistory() {
        rentalDao.clearAll()
        purchaseDao.insertAll(emptyList())
    }
}