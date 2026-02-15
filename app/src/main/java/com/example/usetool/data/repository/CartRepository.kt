package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class CartRepository(
    private val dataSource: DataSource,
    private val cartDao: CartDao,
    private val toolDao: ToolDao,
    private val slotDao: SlotDao
) {
    // Flag per impedire alla sync remota di sovrascrivere modifiche locali non ancora propagate
    private var isLocalOperationActive = false

    fun getActiveCart(userId: String): Flow<CartEntity?> = cartDao.getActiveCart(userId)

    fun getLocalCartItems(cartId: String): Flow<List<CartItemEntity>> = cartDao.getItemsByCartId(cartId)

    suspend fun syncCartFromNetwork(userId: String) {
        dataSource.observeUserCart(userId).collectLatest { cartDto ->
            // Se c'è un'operazione locale (aggiunta/rimozione), ignoriamo il network per evitare flickering
            if (isLocalOperationActive) return@collectLatest

            if (cartDto == null) {
                cartDao.clearCartLocally("cart_$userId")
                return@collectLatest
            }

            val safeCartId = cartDto.id ?: "cart_$userId"
            val toolsMap = toolDao.getAllTools().firstOrNull()?.associateBy { it.id } ?: emptyMap()

            val itemEntities = cartDto.items.map { (slotId, slotDto) ->
                val tool = toolsMap[slotDto.toolId]
                // Allineiamo lo stato dello slot in Room con il contenuto del carrello
                slotDao.updateSlotStatus(slotId, "IN_CARRELLO")

                CartItemEntity(
                    cartId = safeCartId,
                    slotId = slotId,
                    toolId = slotDto.toolId,
                    lockerId = "", // Può essere popolato via join se necessario
                    toolName = tool?.name ?: "Strumento",
                    price = tool?.price ?: 0.0,
                    quantity = 1
                )
            }
            cartDao.updateFullCart(cartDto.toEntity().copy(id = safeCartId), itemEntities)
        }
    }

    suspend fun addItemToCart(userId: String, tool: ToolEntity, slot: SlotEntity) {
        isLocalOperationActive = true
        try {
            val safeCartId = "cart_$userId"
            val localCart = cartDao.getActiveCart(userId).firstOrNull()

            // 1. Aggiornamento UI Immediato in Room
            slotDao.updateSlotStatus(slot.id, "IN_CARRELLO")

            val currentItems = cartDao.getItemsByCartId(safeCartId).firstOrNull() ?: emptyList()
            val newItem = CartItemEntity(
                cartId = safeCartId,
                slotId = slot.id,
                toolId = tool.id,
                lockerId = slot.lockerId,
                toolName = tool.name,
                price = tool.price
            )

            val updatedList = currentItems + newItem
            val newTotal = updatedList.sumOf { it.price }
            val newCart = CartEntity(id = safeCartId, userId = userId, totaleProvvisorio = newTotal)

            cartDao.updateFullCart(newCart, updatedList)

            // 2. Aggiornamento Firebase
            val itemsMap = updatedList.associate { it.slotId to SlotDTO(id = it.slotId, toolId = it.toolId, status = "IN_CARRELLO") }
            val cartDto = newCart.toDto(itemsMap)

            dataSource.updateMultipleNodes(mapOf(
                "slots/${slot.id}/status" to "IN_CARRELLO",
                "carts/$userId" to cartDto
            ))

            // Aspettiamo un attimo che Firebase si stabilizzi prima di riattivare la sync
            delay(1000)
        } finally {
            isLocalOperationActive = false
        }
    }

    suspend fun removeItemFromCart(userId: String, slotId: String) {
        isLocalOperationActive = true
        try {
            val safeCartId = "cart_$userId"
            val localCart = cartDao.getActiveCart(userId).firstOrNull() ?: return
            val currentItems = cartDao.getItemsByCartId(safeCartId).firstOrNull() ?: emptyList()

            // 1. Aggiornamento UI Immediato
            val updatedList = currentItems.filter { it.slotId != slotId }
            val newTotal = updatedList.sumOf { it.price }

            slotDao.updateSlotStatus(slotId, "DISPONIBILE")
            cartDao.updateFullCart(localCart.copy(totaleProvvisorio = newTotal), updatedList)

            // 2. Aggiornamento Firebase
            val itemsMap = updatedList.associate { it.slotId to SlotDTO(id = it.slotId, toolId = it.toolId, status = "IN_CARRELLO") }
            val cartDto = localCart.copy(totaleProvvisorio = newTotal).toDto(itemsMap)

            dataSource.updateMultipleNodes(mapOf(
                "slots/$slotId/status" to "DISPONIBILE",
                "carts/$userId" to cartDto
            ))
            delay(1000)
        } finally {
            isLocalOperationActive = false
        }
    }

    suspend fun clearLocalCart(cartId: String) {
        cartDao.clearCartLocally(cartId)
    }
}