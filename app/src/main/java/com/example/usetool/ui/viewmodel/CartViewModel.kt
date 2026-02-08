package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.usetool.data.dto.SlotDTO
import com.example.usetool.data.dto.ToolDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// Oggetto carrello che usa i DTO
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val tool: ToolDTO,
    val slot: SlotDTO,
    var durationHours: Int = 24 // Solo per noleggio
) {
    val subtotal: Double get() = if (tool.type == "noleggio") tool.price * durationHours else tool.price
}

class CartViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    val total: Double get() = _items.value.sumOf { it.subtotal }

    fun add(tool: ToolDTO, slot: SlotDTO) {
        // Evita duplicati dello stesso cassetto
        if (_items.value.none { it.slot.id == slot.id }) {
            _items.value += CartItem(tool = tool, slot = slot)
        }
    }

    fun remove(itemId: String) {
        _items.value = _items.value.filterNot { it.id == itemId }
    }

    fun checkout() {
        // Qui implementerai la generazione di Noleggio/Acquisto su Firebase
        _items.value = emptyList()
    }
}