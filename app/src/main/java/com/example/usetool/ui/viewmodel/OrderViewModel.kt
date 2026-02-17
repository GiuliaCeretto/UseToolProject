package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.repository.OrderRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository = Injection.provideOrderRepository()
) : ViewModel() {

    // Osserva gli acquisti (PurchaseEntity) salvati nel DB locale
    val localPurchases = orderRepository.localPurchases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Osserva i noleggi (RentalEntity) salvati nel DB locale
    val localRentals = orderRepository.localRentals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Conferma il ritiro fisico dell'acquisto
    fun confirmPurchasePickup(userId: String, purchaseId: String) {
        viewModelScope.launch {
            orderRepository.confirmPurchasePickup(userId, purchaseId)
        }
    }

    // Avvia il periodo di noleggio (ritiro dello strumento)
    fun confirmStartRental(userId: String, rentalId: String) {
        viewModelScope.launch {
            orderRepository.confirmStartRental(userId, rentalId)
        }
    }
}