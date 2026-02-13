package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.SlotEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.data.repository.CartRepository
import com.example.usetool.data.repository.OrderRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(
    private val cartRepository: CartRepository = Injection.provideCartRepository(),
    private val orderRepository: OrderRepository = Injection.provideOrderRepository()
) : ViewModel() {

    private val userId = "user_demo_123"

    // Espone lo stato della testata (CartEntity) recuperata dal DAO
    val cartHeader = cartRepository.getActiveCart(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Espone la lista degli elementi (CartItemEntity) recuperata dal DAO
    val cartItems = cartHeader.flatMapLatest { cart ->
        if (cart != null) cartRepository.getLocalCartItems(cart.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Riceve solo Entity
    fun addToolToCart(tool: ToolEntity, slot: SlotEntity) {
        viewModelScope.launch {
            cartRepository.addItemToCart(userId, tool, slot)
        }
    }

    fun removeItem(slotId: String) {
        viewModelScope.launch {
            cartRepository.removeItemFromCart(userId, slotId)
        }
    }

    fun performCheckout() {
        val currentCart = cartHeader.value ?: return
        viewModelScope.launch {
            // Passa l'Entity al repository; il mapping in DTO avverrà lì dentro
            orderRepository.processCheckout(userId, currentCart)
        }
    }
}