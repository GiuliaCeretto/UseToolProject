package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.CartDTO
import com.example.usetool.data.dto.SlotDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.repository.CartRepository
import com.example.usetool.data.repository.OrderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class) // RISOLTO: Sì, l'Opt-in è necessario qui
class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val userId = "user_demo_123"

    // Il flusso parte al primo ascolto e rimane attivo finché il ViewModel esiste
    val cartHeader = cartRepository.getActiveCart(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    // Anche per gli elementi del carrello usiamo Lazily per coerenza
    val cartItems = cartHeader.flatMapLatest { cart ->
        if (cart != null) cartRepository.getLocalCartItems(cart.id)
        else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun addToolToCart(tool: ToolDTO, slot: SlotDTO) {
        viewModelScope.launch {
            try {
                cartRepository.addItemToCart(userId, tool, slot)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeItem(slotId: String, allTools: List<ToolDTO>) {
        viewModelScope.launch {
            try {
                cartRepository.removeItemFromCart(userId, slotId, allTools)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun performCheckout(currentCart: CartDTO, toolsList: List<ToolDTO>) {
        viewModelScope.launch {
            try {
                orderRepository.processCheckout(userId, currentCart, toolsList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}