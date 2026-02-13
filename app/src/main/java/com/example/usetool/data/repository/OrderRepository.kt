package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.UseToolService
import kotlinx.coroutines.flow.Flow

class OrderRepository(
    private val dataSource: DataSource,
    private val uTservice: UseToolService
) {
    fun getUserPurchases(userId: String): Flow<List<PurchaseDTO>> = dataSource.observePurchases(userId)
    fun getUserRentals(userId: String): Flow<List<RentalDTO>> = dataSource.observeRentals(userId)

    suspend fun processCheckout(userId: String, cart: CartDTO, toolsList: List<ToolDTO>) {
        val updates = mutableMapOf<String, Any?>()

        cart.items.forEach { (slotId, slot) ->
            val tool = toolsList.find { it.id == slot.toolId } ?: throw Exception("Tool non trovato")

            if (tool.type == "acquisto") {
                val purchaseKey = "p_${System.currentTimeMillis()}_$slotId"
                updates["purchases/$userId/$purchaseKey"] = PurchaseDTO(
                    id = purchaseKey, userId = userId, cartId = cart.id, toolId = tool.id,
                    toolName = tool.name, prezzoPagato = tool.price,
                    lockerId = slot.lockerId, slotId = slotId
                )
                updates["slots/$slotId/status"] = "VUOTO"
            } else {
                val rentalKey = "r_${System.currentTimeMillis()}_$slotId"
                updates["rentals/$userId/$rentalKey"] = RentalDTO(
                    id = rentalKey, userId = userId, toolId = tool.id, toolName = tool.name,
                    lockerId = slot.lockerId, slotId = slotId,
                    costoTotale = 20.0, statoNoleggio = "ATTIVO"
                )
                updates["slots/$slotId/status"] = "NOLEGGIATO"
            }
        }

        // Segna il carrello come confermato
        updates["carts/$userId"] = CartDTO(userId = userId, status = "CONFERMATO")
        uTservice.updateMultipleNodes(updates)
    }
}