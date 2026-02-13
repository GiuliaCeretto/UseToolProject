package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class OrderRepository(
    private val dataSource: DataSource,
    private val purchaseDao: PurchaseDao,
    private val rentalDao: RentalDao,
    private val cartRepository: CartRepository // Iniezione necessaria per pulire il carrello
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

    suspend fun processCheckout(userId: String, cart: CartDTO, toolsList: List<ToolDTO>) {
        val updates = mutableMapOf<String, Any?>()
        val now = System.currentTimeMillis()

        cart.items.forEach { (slotId, slot) ->
            val tool = toolsList.find { it.id == slot.toolId } ?: return@forEach
            if (tool.type == "acquisto") {
                val pKey = "p_${now}_$slotId"
                updates["purchases/$userId/$pKey"] = PurchaseDTO(id = pKey, toolName = tool.name, prezzoPagato = tool.price, dataAcquisto = now, lockerId = slot.lockerId)
                updates["slots/$slotId/status"] = "VUOTO"
            } else {
                val rKey = "r_${now}_$slotId"
                updates["rentals/$userId/$rKey"] = RentalDTO(id = rKey, userId = userId, toolId = tool.id, toolName = tool.name, lockerId = slot.lockerId, slotId = slotId, dataInizio = now, statoNoleggio = "ATTIVO")
                updates["slots/$slotId/status"] = "NOLEGGIATO"
            }
        }

        updates["carts/$userId"] = CartDTO(userId = userId, status = "CONFERMATO")

        // Esegue l'aggiornamento atomico su Firebase
        dataSource.updateMultipleNodes(updates)

        // RISOLTO: Ora usiamo clearLocalCart per svuotare Room dopo il checkout
        cart.id?.let { cartId ->
            cartRepository.clearLocalCart(cartId)
        }
    }

    suspend fun clearOrderHistory() {
        rentalDao.clearAll()
        purchaseDao.insertAll(emptyList()) // Esempio di pulizia anche per acquisti
    }
}