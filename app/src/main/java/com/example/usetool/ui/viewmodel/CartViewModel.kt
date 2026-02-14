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

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    val cartHeader = cartRepository.getActiveCart(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val cartItems = cartHeader.flatMapLatest { cart ->
        if (cart != null) cartRepository.getLocalCartItems(cart.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addToolToCart(tool: ToolEntity, slot: SlotEntity) {
        viewModelScope.launch {
            try {
                cartRepository.addItemToCart(userId, tool, slot)
            } catch (_: Exception) {
                _errorMessage.emit("Impossibile aggiungere al carrello")
            }
        }
    }

    fun removeItem(slotId: String) {
        viewModelScope.launch {
            try {
                cartRepository.removeItemFromCart(userId, slotId)
            } catch (_: Exception) {
                _errorMessage.emit("Errore durante la rimozione dell'articolo")
            }
        }
    }

    fun performCheckout() {
        val currentCart = cartHeader.value ?: return
        viewModelScope.launch {
            try {
                orderRepository.processCheckout(userId, currentCart)
            } catch (_: Exception) {
                _errorMessage.emit("Checkout fallito. Riprova più tardi.")
            }
        }
    }
}