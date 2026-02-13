package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.CartDTO
import com.example.usetool.data.dto.SlotDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.repository.CartRepository
import com.example.usetool.data.repository.OrderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val tool: ToolDTO,
    val slot: SlotDTO,
    var durationHours: Int = 24
) {
    val subtotal: Double get() = if (tool.type == "noleggio") tool.price * durationHours else tool.price
}

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val userId = "user_demo_123" // Da recuperare tramite Auth

    // Osserva il carrello remoto convertendolo in CartItem per la UI
    val items: StateFlow<List<CartItem>> = cartRepository.getUserCart(userId)
        .map { dto ->
            dto?.items?.values?.map { slot ->
                // Nota: In un caso reale, qui servirebbe una lista di Tool per mappare i dettagli
                CartItem(tool = ToolDTO(id = slot.toolId, name = "Tool ${slot.toolId}", price = 10.0, quantity = 1), slot = slot)
            } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val total: Double get() = items.value.sumOf { it.subtotal }

    fun add(tool: ToolDTO, slot: SlotDTO) {
        viewModelScope.launch {
            try {
                cartRepository.addItemToCart(userId, tool, slot)
            } catch (e: Exception) {
                // Gestire errore (es. slot occupato)
            }
        }
    }

    fun remove(slotId: String, allTools: List<ToolDTO>) {
        viewModelScope.launch {
            cartRepository.removeItemFromCart(userId, slotId, allTools)
        }
    }

    fun checkout(currentCart: CartDTO, toolsList: List<ToolDTO>) {
        viewModelScope.launch {
            orderRepository.processCheckout(userId, currentCart, toolsList)
        }
    }
}