package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CartRepository(
    private val dataSource: DataSource,
    private val cartDao: CartDao,
    private val toolDao: ToolDao
) {
    fun getActiveCart(userId: String): Flow<CartEntity?> = cartDao.getActiveCart(userId)

    fun getLocalCartItems(cartId: String): Flow<List<CartItemEntity>> =
        cartDao.getItemsByCartId(cartId)

    suspend fun addItemToCart(userId: String, tool: ToolEntity, slot: SlotEntity) {
        val currentCartDto = dataSource.observeUserCart(userId).firstOrNull()
            ?: CartDTO(userId = userId, id = "cart_$userId", status = "PENDING")

        val updatedItems = currentCartDto.items.toMutableMap()
        updatedItems[slot.id] = slot.toDto()

        val nuovoCarrelloDto = currentCartDto.copy(
            items = updatedItems,
            totaleProvvisorio = currentCartDto.totaleProvvisorio + tool.price,
            ultimoAggiornamento = System.currentTimeMillis()
        )

        val updates = mapOf(
            "slots/${slot.id}/status" to "IN_CARRELLO",
            "carts/$userId" to nuovoCarrelloDto
        )

        dataSource.updateMultipleNodes(updates)

        // Conversione per Room con gestione atomica
        val itemEntities = nuovoCarrelloDto.items.map { (slotId, sDto) ->
            CartItemEntity(
                cartId = nuovoCarrelloDto.id ?: "",
                slotId = slotId,
                toolId = sDto.toolId,
                toolName = tool.name,
                price = tool.price
            )
        }
        cartDao.updateFullCart(nuovoCarrelloDto.toEntity(), itemEntities)
    }

    suspend fun removeItemFromCart(userId: String, slotId: String) {
        val currentCartDto = dataSource.observeUserCart(userId).firstOrNull()
            ?: throw IllegalStateException("Impossibile trovare il carrello sul server.")

        val allTools = toolDao.getAllTools().firstOrNull() ?: emptyList()

        val updatedItems = currentCartDto.items.toMutableMap()
        updatedItems.remove(slotId)

        val newTotal = updatedItems.values.sumOf { s ->
            allTools.find { it.id == s.toolId }?.price ?: 0.0
        }

        val nuovoCarrelloDto = currentCartDto.copy(items = updatedItems, totaleProvvisorio = newTotal)

        val updates = mapOf(
            "slots/$slotId/status" to "DISPONIBILE",
            "carts/$userId" to nuovoCarrelloDto
        )

        dataSource.updateMultipleNodes(updates)

        val itemEntities = nuovoCarrelloDto.items.map { (sId, sDto) ->
            val tool = allTools.find { it.id == sDto.toolId }
            CartItemEntity(
                cartId = nuovoCarrelloDto.id ?: "",
                slotId = sId,
                toolId = sDto.toolId,
                toolName = tool?.name ?: "Unknown",
                price = tool?.price ?: 0.0
            )
        }
        cartDao.updateFullCart(nuovoCarrelloDto.toEntity(), itemEntities)
    }

    suspend fun clearLocalCart(cartId: String) {
        cartDao.clearCartLocally(cartId)
    }
}