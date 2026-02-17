package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.CartItemEntity
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
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val cartItems = cartHeader.flatMapLatest { cart ->
        if (cart != null) {
            cartRepository.getLocalCartItems(cart.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        refreshCart()
    }

    fun refreshCart() {
        if (userId.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    cartRepository.syncCartFromNetwork(userId)
                } catch (e: Exception) {
                    _errorMessage.emit("Errore sincronizzazione carrello")
                }
            }
        }
    }

    fun updateItemQuantity(slotId: String, newQuantity: Int) {
        if (userId.isEmpty() || _isProcessing.value || newQuantity < 1) return
        viewModelScope.launch {
            try {
                cartRepository.updateItemQuantity(userId, slotId, newQuantity)
            } catch (e: Exception) {
                _errorMessage.emit("Impossibile aggiornare quantità")
            }
        }
    }

    fun addMultipleToolsToCart(toolsWithSlots: List<Pair<ToolEntity, SlotEntity>>) {
        if (userId.isEmpty() || _isProcessing.value || toolsWithSlots.isEmpty()) return
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                cartRepository.addMultipleItemsToCart(userId, toolsWithSlots)
            } catch (e: Exception) {
                _errorMessage.emit("Errore nell'aggiunta: ${e.message}")
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun addToolToCart(tool: ToolEntity, slot: SlotEntity) {
        addMultipleToolsToCart(listOf(tool to slot))
    }

    fun removeItem(slotId: String) {
        if (userId.isEmpty() || _isProcessing.value) return
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                cartRepository.removeItemFromCart(userId, slotId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore nella rimozione")
            } finally {
                _isProcessing.value = false
            }
        }
    }

    /**
     * Esegue il checkout parziale per gli articoli di un locker specifico.
     * Il filtraggio ora confronta direttamente la stringa lockerId (linkId).
     */
    fun performCheckout(lockerId: Int, onSuccess: (List<String>) -> Unit) {
        val currentCart = cartHeader.value ?: return
        val allItems = cartItems.value

        val itemsToPay = allItems.filter { it.lockerId == lockerId.toString() }

        if (_isProcessing.value || itemsToPay.isEmpty()) return

        viewModelScope.launch {
            _isProcessing.value = true
            try {
                // 1. Processiamo il pagamento solo per gli oggetti del locker attuale
                val rentalIds = orderRepository.processCheckout(userId, currentCart, itemsToPay)

                // 2. Rimuoviamo dal carrello locale SOLO gli articoli effettivamente pagati
                itemsToPay.forEach { item ->
                    cartRepository.removeItemFromCart(userId, item.slotId)
                }

                _errorMessage.emit("Pagamento completato con successo!")
                onSuccess(rentalIds)
            } catch (e: Exception) {
                _errorMessage.emit("Errore durante il pagamento: ${e.message}")
            } finally {
                _isProcessing.value = false
            }
        }
    }
}