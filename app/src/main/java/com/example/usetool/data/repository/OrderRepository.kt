package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toPurchaseEntityList
import com.example.usetool.data.service.toRentalEntityList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class OrderRepository(
    private val dataSource: DataSource,
    private val purchaseDao: PurchaseDao,
    private val rentalDao: RentalDao,
    private val cartDao: CartDao,
    private val toolDao: ToolDao,
    private val cartRepository: CartRepository,
    private val slotDao: SlotDao
) {
    val localPurchases: Flow<List<PurchaseEntity>> = purchaseDao.getAll()
    val localRentals: Flow<List<RentalEntity>> = rentalDao.getAllRentals()

    /**
     * Sincronizza lo storico ordini.
     * NOTA: Utilizziamo coroutineScope per evitare che collectLatest blocchi l'esecuzione.
     */
    suspend fun syncUserOrders(userId: String) = coroutineScope {
        launch {
            dataSource.observePurchases(userId).collectLatest { list ->
                // Usa insertAll che gestisce REPLACE, ma assicurati di non svuotare
                if (list.isNotEmpty()) {
                    purchaseDao.insertAll(list.toPurchaseEntityList())
                }
            }
        }
        launch {
            dataSource.observeRentals(userId).collectLatest { list ->
                if (list.isNotEmpty()) {
                    rentalDao.insertRentals(list.toRentalEntityList())
                }
            }
        }
    }
    /**
     * Checkout: Trasforma gli item del carrello in oggetti Noleggio o Acquisto.
     */
    suspend fun processCheckout(userId: String, cart: CartEntity) {
        val updates = mutableMapOf<String, Any?>()
        val now = System.currentTimeMillis()

        val items = cartDao.getItemsByCartId(cart.id).firstOrNull()
            ?: throw IllegalStateException("Carrello vuoto")

        val toolsList = toolDao.getAllTools().firstOrNull() ?: emptyList()
        val slotsList = slotDao.getAllSlots().firstOrNull() ?: emptyList()

        items.forEach { item ->
            val tool = toolsList.find { it.id == item.toolId } ?: return@forEach
            val slot = slotsList.find { it.id == item.slotId } ?: return@forEach

            if (tool.type == "acquisto") {
                // LOGICA ACQUISTO
                val pKey = "p_${now}_${item.slotId}"
                updates["purchases/$userId/$pKey"] = PurchaseDTO(
                    id = pKey,
                    userId = userId,
                    toolId = tool.id,
                    toolName = tool.name,
                    prezzoPagato = item.price,
                    dataAcquisto = now,
                    dataRitiro = 0L,
                    lockerId = slot.lockerId,
                    slotId = slot.id
                )

                // Scarico quantità reattivo
                val newQty = (slot.quantity - 1).coerceAtLeast(0)
                updates["slots/${slot.id}/quantity"] = newQty
                updates["slots/${slot.id}/status"] = if (newQty <= 0) "VUOTO" else "DISPONIBILE"

                // Aggiornamento locale immediato per la UI
                slotDao.updateSlotStatus(slot.id, if (newQty <= 0) "VUOTO" else "DISPONIBILE")

            } else {
                // LOGICA NOLEGGIO
                val rKey = "r_${now}_${item.slotId}"
                updates["rentals/$userId/$rKey"] = RentalDTO(
                    id = rKey,
                    userId = userId,
                    toolId = tool.id,
                    toolName = tool.name,
                    slotId = item.slotId,
                    lockerId = slot.lockerId,
                    dataInizio = 0L,
                    dataFinePrevista = 0L,
                    statoNoleggio = "PRENOTATO",
                    costoTotale = item.price
                )
                updates["slots/${item.slotId}/status"] = "NOLEGGIATO"

                // Aggiornamento locale immediato per la UI
                slotDao.updateSlotStatus(item.slotId, "NOLEGGIATO")
            }
        }

        // Pagamento
        val payKey = "pay_${now}"
        updates["payments/$userId/$payKey"] = mapOf(
            "id_carrello" to cart.id,
            "totale" to cart.totaleProvvisorio,
            "stato" to "CONFERMATO",
            "timestamp" to now
        )

        // Reset carrello su Firebase
        updates["carts/$userId"] = CartDTO(userId = userId, id = "cart_$userId", status = "CONFERMATO")

        // Push atomico su Firebase
        dataSource.updateMultipleNodes(updates)

        // Pulizia locale
        cartRepository.clearLocalCart(cart.id)
    }

    /**
     * Conferma il ritiro (Apertura cassetto per acquisto)
     */
    suspend fun confirmPurchasePickup(userId: String, purchaseId: String) {
        val updates = mapOf("purchases/$userId/$purchaseId/dataRitiro" to System.currentTimeMillis())
        dataSource.updateMultipleNodes(updates)
    }

    /**
     * Conferma inizio noleggio (Apertura cassetto)
     */
    suspend fun confirmStartRental(userId: String, rentalId: String) {
        val updates = mapOf(
            "rentals/$userId/$rentalId/dataInizio" to System.currentTimeMillis(),
            "rentals/$userId/$rentalId/statoNoleggio" to "ATTIVO"
        )
        dataSource.updateMultipleNodes(updates)
    }

    /**
     * Conferma riconsegna (Chiusura cassetto e liberazione slot)
     */
    suspend fun confirmEndRental(userId: String, rentalId: String, slotId: String) {
        val updates = mapOf(
            "rentals/$userId/$rentalId/dataFineEffettiva" to System.currentTimeMillis(),
            "rentals/$userId/$rentalId/statoNoleggio" to "CONCLUSO",
            "slots/$slotId/status" to "DISPONIBILE"
        )
        dataSource.updateMultipleNodes(updates)
        slotDao.updateSlotStatus(slotId, "DISPONIBILE")
    }

    suspend fun clearOrderHistory() {
        rentalDao.clearAll()
        purchaseDao.clearAll()
    }
}