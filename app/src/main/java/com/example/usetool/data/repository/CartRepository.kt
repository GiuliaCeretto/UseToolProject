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
    private val slotDao: SlotDao,
    private val lockerDao: LockerDao
) {
    // Flag per evitare che il sync dal network sovrascriva operazioni locali in corso
    private var isLocalOperationActive = false

    fun getActiveCart(userId: String): Flow<CartEntity?> = cartDao.getActiveCart(userId)

    fun getLocalCartItems(cartId: String): Flow<List<CartItemEntity>> = cartDao.getItemsByCartId(cartId)

    /**
     * Sincronizza il carrello da Firebase a Room (Database locale).
     * Mappa l'ID stringa del locker nel linkId numerico per permettere lo sblocco e il filtraggio.
     */
    suspend fun syncCartFromNetwork(userId: String) {
        dataSource.observeUserCart(userId).collectLatest { cartDto ->
            if (isLocalOperationActive) return@collectLatest

            if (cartDto == null) {
                cartDao.clearCartLocally("cart_$userId")
                return@collectLatest
            }

            val safeCartId = cartDto.id ?: "cart_$userId"

            // Snapshot dei dati necessari per il mapping
            val toolsMap = toolDao.getAllTools().firstOrNull()?.associateBy { it.id } ?: emptyMap()
            val lockersMap = lockerDao.getAll().firstOrNull()?.associateBy { it.id } ?: emptyMap()
            val slotsMap = slotDao.getAllSlots().firstOrNull()?.associateBy { it.id } ?: emptyMap()

            val itemEntities = cartDto.items.map { (slotId, slotDto) ->
                val tool = toolsMap[slotDto.toolId]
                val slot = slotsMap[slotId]
                val locker = lockersMap[slot?.lockerId]

                slotDao.updateSlotStatus(slotId, "IN_CARRELLO")

                CartItemEntity(
                    cartId = safeCartId,
                    slotId = slotId,
                    toolId = slotDto.toolId,
                    // Salviamo il linkId (Int) come stringa per coerenza con il sistema di sblocco
                    lockerId = locker?.linkId?.toString() ?: "",
                    toolName = tool?.name ?: "Strumento",
                    price = tool?.price ?: 0.0,
                    quantity = slotDto.quantity
                )
            }

            cartDao.updateFullCart(cartDto.toEntity().copy(id = safeCartId), itemEntities)
        }
    }

    /**
     * Aggiorna la quantità di un singolo articolo nel carrello.
     */
    suspend fun updateItemQuantity(userId: String, slotId: String, newQuantity: Int) {
        if (newQuantity < 1) return

        isLocalOperationActive = true
        try {
            val safeCartId = "cart_$userId"
            val currentHeader = cartDao.getActiveCart(userId).firstOrNull() ?: return
            val currentItems = cartDao.getItemsByCartId(safeCartId).firstOrNull()?.toMutableList() ?: return

            val index = currentItems.indexOfFirst { it.slotId == slotId }
            if (index != -1) {
                currentItems[index] = currentItems[index].copy(quantity = newQuantity)

                val newTotal = currentItems.sumOf { it.price * it.quantity }
                val updatedHeader = currentHeader.copy(totaleProvvisorio = newTotal)

                cartDao.updateFullCart(updatedHeader, currentItems)

                val itemsMap = currentItems.associate {
                    it.slotId to SlotDTO(
                        id = it.slotId, toolId = it.toolId, status = "IN_CARRELLO", quantity = it.quantity
                    )
                }

                val updates = mapOf(
                    "carts/$userId" to updatedHeader.toDto(itemsMap),
                    "slots/$slotId/quantity" to newQuantity
                )
                dataSource.updateMultipleNodes(updates)
            }
        } finally {
            isLocalOperationActive = false
        }
    }

    /**
     * Aggiunta batch di più strumenti al carrello.
     * Recupera il linkId dal locker per popolare correttamente la CartItemEntity.
     */
    suspend fun addMultipleItemsToCart(userId: String, items: List<Pair<ToolEntity, SlotEntity>>) {
        if (items.isEmpty()) return
        isLocalOperationActive = true
        try {
            val safeCartId = "cart_$userId"
            val currentItems = cartDao.getItemsByCartId(safeCartId).firstOrNull()?.toMutableList() ?: mutableListOf()
            val allLockers = lockerDao.getAll().firstOrNull() ?: emptyList()
            val updates = mutableMapOf<String, Any?>()

            val itemsGrouped = items.groupBy { it.second.id }

            itemsGrouped.forEach { (slotId, pairs) ->
                val tool = pairs.first().first
                val slot = pairs.first().second
                val quantityToAdd = pairs.size

                val existingItemIndex = currentItems.indexOfFirst { it.slotId == slotId }

                // Cerchiamo il linkId numerico del locker
                val locker = allLockers.find { it.id == slot.lockerId }
                val numericLockerId = locker?.linkId?.toString() ?: ""

                if (existingItemIndex != -1) {
                    currentItems[existingItemIndex] = currentItems[existingItemIndex].copy(
                        quantity = quantityToAdd
                    )
                } else {
                    currentItems.add(CartItemEntity(
                        cartId = safeCartId,
                        slotId = slotId,
                        toolId = tool.id,
                        lockerId = numericLockerId, // Utilizzo linkId numerico
                        toolName = tool.name,
                        price = tool.price,
                        quantity = quantityToAdd
                    ))
                }
                slotDao.updateSlotStatus(slotId, "IN_CARRELLO")
                updates["slots/$slotId/status"] = "IN_CARRELLO"
                updates["slots/$slotId/quantity"] = quantityToAdd
            }

            val newTotal = currentItems.sumOf { it.price * it.quantity }
            val updatedCart = CartEntity(id = safeCartId, userId = userId, totaleProvvisorio = newTotal)

            cartDao.updateFullCart(updatedCart, currentItems)

            val itemsMapForFirebase = currentItems.associate {
                it.slotId to SlotDTO(
                    id = it.slotId, toolId = it.toolId, status = "IN_CARRELLO", quantity = it.quantity
                )
            }

            updates["carts/$userId"] = updatedCart.toDto(itemsMapForFirebase)
            dataSource.updateMultipleNodes(updates)
            delay(500)
        } finally {
            isLocalOperationActive = false
        }
    }

    /**
     * Rimuove un prodotto dal carrello e ripristina lo slot.
     */
    suspend fun removeItemFromCart(userId: String, slotId: String) {
        isLocalOperationActive = true
        try {
            val safeCartId = "cart_$userId"
            val localCart = cartDao.getActiveCart(userId).firstOrNull() ?: return
            val currentItems = cartDao.getItemsByCartId(safeCartId).firstOrNull() ?: emptyList()

            val updatedList = currentItems.filter { it.slotId != slotId }
            val newTotal = updatedList.sumOf { it.price * it.quantity }
            val updatedCartHeader = localCart.copy(totaleProvvisorio = newTotal)

            slotDao.updateSlotStatus(slotId, "DISPONIBILE")
            cartDao.updateFullCart(updatedCartHeader, updatedList)

            val itemsMap = updatedList.associate {
                it.slotId to SlotDTO(
                    id = it.slotId, toolId = it.toolId, status = "IN_CARRELLO", quantity = it.quantity
                )
            }

            dataSource.updateMultipleNodes(mapOf(
                "slots/$slotId/status" to "DISPONIBILE",
                "slots/$slotId/quantity" to 1,
                "carts/$userId" to updatedCartHeader.toDto(itemsMap)
            ))

            delay(500)
        } finally {
            isLocalOperationActive = false
        }
    }

    suspend fun clearLocalCart(cartId: String) {
        isLocalOperationActive = true
        try {
            cartDao.clearCartLocally(cartId)
        } finally {
            isLocalOperationActive = false
        }
    }

    suspend fun addItemToCart(userId: String, tool: ToolEntity, slot: SlotEntity) {
        if (slot.quantity <= 0) return
        addMultipleItemsToCart(userId, listOf(tool to slot))
    }
}