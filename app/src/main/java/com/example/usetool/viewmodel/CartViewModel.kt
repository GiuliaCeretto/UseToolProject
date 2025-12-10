package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.CartItem
import com.example.usetool.model.Tool
import java.util.UUID

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    val total: Double get() = _items.value.sumOf { it.subtotal }

    fun add(tool: Tool, distributorId: String?) {
        val new = CartItem(UUID.randomUUID().toString(), tool, distributorId)
        _items.value = _items.value + new
    }

    fun remove(itemId: String) {
        _items.value = _items.value.filterNot { it.id == itemId }
    }

    fun clear() {
        _items.value = emptyList()
    }
}
