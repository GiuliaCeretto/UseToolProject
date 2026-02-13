package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.UseToolService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CartRepository(
    private val dataSource: DataSource,
    private val uTservice: UseToolService
) {
    fun getUserCart(userId: String): Flow<CartDTO?> = dataSource.observeUserCart(userId)

    suspend fun addItemToCart(userId: String, tool: ToolDTO, slot: SlotDTO) {
        val allSlots = dataSource.observeSlots().firstOrNull()
        val latestSlot = allSlots?.find { it.id == slot.id }

        if (latestSlot?.status != "DISPONIBILE") {
            throw Exception("L'attrezzo è stato appena occupato da un altro utente")
        }

        val currentCart = dataSource.observeUserCart(userId).firstOrNull() ?: CartDTO(userId = userId)
        val updatedItems = currentCart.items.toMutableMap()
        updatedItems[slot.id] = slot

        val nuovoCarrello = currentCart.copy(
            items = updatedItems,
            totaleProvvisorio = currentCart.totaleProvvisorio + tool.price,
            status = "ATTIVO"
        )

        // Aggiornamento atomico: slot in carrello + aggiornamento carrello
        val updates = mapOf(
            "slots/${slot.id}/status" to "IN_CARRELLO",
            "carts/$userId" to nuovoCarrello
        )
        uTservice.updateMultipleNodes(updates)
    }

    suspend fun removeItemFromCart(userId: String, slotId: String, allTools: List<ToolDTO>) {
        val currentCart = dataSource.observeUserCart(userId).firstOrNull() ?: return
        val updatedItems = currentCart.items.toMutableMap()
        updatedItems.remove(slotId)

        val newTotal = updatedItems.values.sumOf { s ->
            allTools.find { it.id == s.toolId }?.price ?: 0.0
        }

        val updates = mapOf(
            "slots/$slotId/status" to "DISPONIBILE",
            "carts/$userId" to currentCart.copy(items = updatedItems, totaleProvvisorio = newTotal)
        )
        uTservice.updateMultipleNodes(updates)
    }

    suspend fun clearCartOptimized(userId: String, cart: CartDTO) {
        val updates = mutableMapOf<String, Any?>()
        cart.items.keys.forEach { updates["slots/$it/status"] = "DISPONIBILE" }
        updates["carts/$userId"] = CartDTO(userId = userId)
        uTservice.updateMultipleNodes(updates)
    }
}