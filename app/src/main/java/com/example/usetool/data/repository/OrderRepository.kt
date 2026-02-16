package com.example.usetool.data.repository

import android.util.Log
import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toPurchaseEntityList
import com.example.usetool.data.service.toRentalEntityList
import com.example.usetool.data.service.toFirebaseMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.util.UUID

class OrderRepository(
    private val dataSource: DataSource,
    private val purchaseDao: PurchaseDao,
    private val rentalDao: RentalDao,
    private val cartDao: CartDao,
    private val toolDao: ToolDao,
    private val cartRepository: CartRepository,
    private val slotDao: SlotDao
) {
    // Flow reattivi per la UI
    val localPurchases: Flow<List<PurchaseEntity>> = purchaseDao.getAll()
    val localRentals: Flow<List<RentalEntity>> = rentalDao.getAllRentals()

    /**
     * Sincronizzazione automatica da Firebase a Room.
     */
    suspend fun syncUserOrders(userId: String) = coroutineScope {
        launch {
            dataSource.observePurchases(userId).collectLatest { list ->
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
     * Esegue il checkout trasformando il carrello in ordini reali.
     * Determina il tipo di transazione (Noleggio vs Vendita) basandosi sul catalogo strumenti.
     */
    suspend fun processCheckout(userId: String, cart: CartEntity, items: List<CartItemEntity>): List<String> = withContext(Dispatchers.IO) {
        val updates = mutableMapOf<String, Any?>()
        val rentalIds = mutableListOf<String>()
        val now = System.currentTimeMillis()

        val rentalsToInsert = mutableListOf<RentalEntity>()
        val purchasesToInsert = mutableListOf<PurchaseEntity>()

        // 🔥 RECUPERO TIPO DAL TOOL DAO:
        // Carichiamo lo snapshot di tutti i tool per mappare i tipi correttamente
        val toolsCatalog = toolDao.getAllTools().first().associateBy { it.id }

        items.forEach { item ->
            val orderId = UUID.randomUUID().toString()
            val costoEffettivo = item.price * item.quantity

            // Recuperiamo l'anagrafica dello strumento dal catalogo
            val toolInfo = toolsCatalog[item.toolId]

            // Verifichiamo il tipo definito nell'anagrafica ToolEntity
            val isRental = toolInfo?.type?.contains("rent", ignoreCase = true) == true ||
                    toolInfo?.type?.contains("noleggio", ignoreCase = true) == true

            if (isRental) {
                // --- CREAZIONE NOLEGGIO ---
                val rental = RentalEntity(
                    id = orderId,
                    userId = userId,
                    toolId = item.toolId,
                    toolName = item.toolName,
                    lockerId = item.lockerId,
                    slotId = item.slotId,
                    dataInizio = now,
                    dataFinePrevista = now + 86400000, // +24 ore
                    dataRiconsegnaEffettiva = null,
                    statoNoleggio = "ATTIVO",
                    costoTotale = costoEffettivo
                )
                rentalsToInsert.add(rental)
                rentalIds.add(orderId)

                updates["rentals/$userId/$orderId"] = rental.toFirebaseMap()
                updates["slots/${item.slotId}/status"] = "PRENOTATO"
                updates["slots/${item.slotId}/quantity"] = item.quantity

                slotDao.updateSlotStatus(item.slotId, "PRENOTATO")
            } else {
                // --- CREAZIONE ACQUISTO ---
                val purchase = PurchaseEntity(
                    id = orderId,
                    userId = userId,
                    cartId = cart.id,
                    toolId = item.toolId,
                    toolName = item.toolName,
                    prezzoPagato = costoEffettivo,
                    dataAcquisto = now,
                    dataRitiro = now + 3600000,
                    dataRitiroEffettiva = null,
                    lockerId = item.lockerId,
                    slotId = item.slotId,
                    idTransazionePagamento = "TX-$now"
                )
                purchasesToInsert.add(purchase)

                updates["purchases/$userId/$orderId"] = purchase.toFirebaseMap()
                updates["slots/${item.slotId}/status"] = "NON_DISPONIBILE"
                updates["slots/${item.slotId}/quantity"] = 0

                slotDao.updateSlotStock(item.slotId, "NON_DISPONIBILE", 0)
            }
        }

        // --- SALVATAGGIO ROOM ---
        if (rentalsToInsert.isNotEmpty()) rentalDao.insertRentals(rentalsToInsert)
        if (purchasesToInsert.isNotEmpty()) purchaseDao.insertAll(purchasesToInsert)

        // --- SALVATAGGIO FIREBASE ---
        updates["carts/$userId"] = null // Svuota il carrello remoto
        dataSource.updateMultipleNodes(updates)

        // --- PULIZIA FINALE ---
        cartDao.deleteItemsByCartId(cart.id)

        rentalIds
    }

    // --- FUNZIONI DI CONFERMA E STATO ---

    suspend fun confirmPurchasePickup(userId: String, purchaseId: String) {
        val updates = mapOf("purchases/$userId/$purchaseId/dataRitiroEffettiva" to System.currentTimeMillis())
        dataSource.updateMultipleNodes(updates)
    }

    suspend fun confirmStartRental(userId: String, rentalId: String) {
        val updates = mapOf(
            "rentals/$userId/$rentalId/dataInizio" to System.currentTimeMillis(),
            "rentals/$userId/$rentalId/statoNoleggio" to "ATTIVO"
        )
        dataSource.updateMultipleNodes(updates)
    }

    suspend fun confirmEndRental(userId: String, rentalId: String, slotId: String) {
        val now = System.currentTimeMillis()
        val updates = mapOf(
            "rentals/$userId/$rentalId/dataRiconsegnaEffettiva" to now,
            "rentals/$userId/$rentalId/statoNoleggio" to "CONCLUSO",
            "slots/$slotId/status" to "DISPONIBILE",
            "slots/$slotId/quantity" to 1
        )
        dataSource.updateMultipleNodes(updates)
        slotDao.updateSlotStock(slotId, "DISPONIBILE", 1)
    }

    suspend fun clearOrderHistory() {
        rentalDao.clearAll()
        purchaseDao.clearAll()
    }

    suspend fun saveLocalRental(rental: RentalEntity) {
        rentalDao.insertRentals(listOf(rental))
    }
}