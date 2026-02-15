package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.SlotEntity
import com.example.usetool.data.dao.ToolEntity
import com.example.usetool.data.repository.*
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(
    private val cartRepository: CartRepository = Injection.provideCartRepository(),
    private val orderRepository: OrderRepository = Injection.provideOrderRepository(),
    private val userRepository: UserRepository = Injection.provideUserRepository()
) : ViewModel() {

    private val userId: String get() = userRepository.getCurrentUserId()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    val cartHeader = cartRepository.getActiveCart(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cartItems = cartHeader.flatMapLatest { cart ->
        if (cart != null) cartRepository.getLocalCartItems(cart.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            if (userId.isNotEmpty()) {
                cartRepository.syncCartFromNetwork(userId)
            }
        }
    }

    fun addToolToCart(tool: ToolEntity, slot: SlotEntity) {
        if (userId.isEmpty() || _isProcessing.value) return
        viewModelScope.launch {
            cartRepository.addItemToCart(userId, tool, slot)
        }
    }

    fun removeItem(slotId: String) {
        if (userId.isEmpty() || _isProcessing.value) return
        viewModelScope.launch {
            cartRepository.removeItemFromCart(userId, slotId)
        }
    }

    fun performCheckout(onSuccess: () -> Unit) {
        val currentCart = cartHeader.value ?: return
        if (_isProcessing.value) return

        viewModelScope.launch {
            _isProcessing.value = true
            try {
                orderRepository.processCheckout(userId, currentCart)
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.emit("Errore checkout: ${e.message}")
            } finally {
                _isProcessing.value = false
            }
        }
    }
}