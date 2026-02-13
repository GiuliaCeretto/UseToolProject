package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.CartDao
import com.example.usetool.data.dao.CartItemEntity
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class CartRepository(
    private val dataSource: DataSource,
    private val cartDao: CartDao,
) {
    // RISOLTO: getActiveCart ora è usato per esporre il carrello alla UI
    fun getActiveCart(userId: String) = cartDao.getActiveCart(userId)

    // RISOLTO: getLocalCartItems è usato per i dettagli del carrello
    fun getLocalCartItems(cartId: String): Flow<List<CartItemEntity>> =
        cartDao.getItemsByCartId(cartId)

    suspend fun addItemToCart(userId: String, tool: ToolDTO, slot: SlotDTO) {
        val currentCartDto = dataSource.observeUserCart(userId).firstOrNull()
            ?: CartDTO(userId = userId, id = "cart_$userId", status = "PENDING")

        val updatedItems = currentCartDto.items.toMutableMap()
        updatedItems[slot.id] = slot

        val nuovoCarrelloDto = currentCartDto.copy(
            items = updatedItems,
            totaleProvvisorio = currentCartDto.totaleProvvisorio + tool.price,
            ultimoAggiornamento = System.currentTimeMillis()
        )

        // Aggiornamento remoto (Firebase)
        val updates = mapOf(
            "slots/${slot.id}/status" to "IN_CARRELLO",
            "carts/$userId" to nuovoCarrelloDto
        )
        dataSource.updateMultipleNodes(updates)

        // RISOLTO: toEntity() ora è usato (Mapper) e sincronizzato in Room
        cartDao.insertCart(nuovoCarrelloDto.toEntity())

        // OTTIMIZZAZIONE: rimosso 'id' inutilizzato nel map per evitare warning
        val itemEntities = nuovoCarrelloDto.items.map { (_, s) ->
            CartItemEntity(
                cartId = nuovoCarrelloDto.id ?: "",
                slotId = s.id,
                toolName = tool.name,
                price = tool.price
            )
        }
        cartDao.insertCartItems(itemEntities)
    }

    suspend fun removeItemFromCart(userId: String, slotId: String, allTools: List<ToolDTO>) {
        val currentCartDto = dataSource.observeUserCart(userId).firstOrNull() ?: return

        val updatedItems = currentCartDto.items.toMutableMap()
        updatedItems.remove(slotId)

        // Ricalcola il totale sottraendo il prezzo dell'attrezzo rimosso
        val newTotal = updatedItems.values.sumOf { s ->
            allTools.find { it.id == s.toolId }?.price ?: 0.0
        }

        val nuovoCarrelloDto = currentCartDto.copy(
            items = updatedItems,
            totaleProvvisorio = newTotal,
            ultimoAggiornamento = System.currentTimeMillis()
        )

        // 1. Aggiornamento atomico Firebase: slot torna DISPONIBILE
        val updates = mapOf(
            "slots/$slotId/status" to "DISPONIBILE",
            "carts/$userId" to nuovoCarrelloDto
        )
        dataSource.updateMultipleNodes(updates)

        // 2. Aggiornamento Room: aggiorna la testata e rigenera la lista elementi
        cartDao.insertCart(nuovoCarrelloDto.toEntity())

        // Pulizia e reinserimento elementi locali per coerenza
        cartDao.deleteItemsByCartId(nuovoCarrelloDto.id ?: "")
        val itemEntities = nuovoCarrelloDto.items.map { (_, s) ->
            val tool = allTools.find { it.id == s.toolId }
            CartItemEntity(
                cartId = nuovoCarrelloDto.id ?: "",
                slotId = s.id,
                toolName = tool?.name ?: "Unknown",
                price = tool?.price ?: 0.0
            )
        }
        cartDao.insertCartItems(itemEntities)
    }

    // RISOLTO: Questa verrà chiamata dall'OrderRepository dopo il checkout
    suspend fun clearLocalCart(cartId: String) {
        cartDao.deleteCart(cartId)
        cartDao.deleteItemsByCartId(cartId)
    }
}