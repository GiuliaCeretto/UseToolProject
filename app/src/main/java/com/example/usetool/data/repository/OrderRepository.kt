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
    private val cartDao: CartDao,
    private val toolDao: ToolDao,
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

    suspend fun processCheckout(userId: String, cart: CartEntity) {
        val updates = mutableMapOf<String, Any?>()
        val now = System.currentTimeMillis()

        // RECUPERO DATI: Se il carrello è vuoto, lanciamo un errore esplicito
        val items = cartDao.getItemsByCartId(cart.id).firstOrNull()
            ?: throw IllegalStateException("Il carrello risulta vuoto. Impossibile procedere.")

        if (items.isEmpty()) throw IllegalStateException("Nessun articolo nel carrello.")

        val toolsList = toolDao.getAllTools().firstOrNull() ?: emptyList()

        items.forEach { item ->
            val tool = toolsList.find { it.name == item.toolName } ?: return@forEach

            if (tool.type == "acquisto") {
                val pKey = "p_${now}_${item.slotId}"
                updates["purchases/$userId/$pKey"] = PurchaseDTO(
                    id = pKey,
                    toolName = tool.name,
                    prezzoPagato = tool.price,
                    dataAcquisto = now,
                    lockerId = "Locker_Default"
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

        updates["carts/$userId"] = CartDTO(userId = userId, status = "CONFERMATO")
        dataSource.updateMultipleNodes(updates)

        // Svuota Room localmente solo dopo il successo su Firebase
        cartRepository.clearLocalCart(cart.id)
    }

    suspend fun clearOrderHistory() {
        rentalDao.clearAll()
        purchaseDao.insertAll(emptyList())
    }
}