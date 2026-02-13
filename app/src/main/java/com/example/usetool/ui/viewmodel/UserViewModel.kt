package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.UserEntity
import com.example.usetool.data.repository.InventoryRepository
import com.example.usetool.data.repository.OrderRepository
import com.example.usetool.data.repository.UserRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository = Injection.provideUserRepository(),
    private val orderRepository: OrderRepository = Injection.provideOrderRepository(),
    private val inventoryRepository: InventoryRepository = Injection.provideInventoryRepository()
) : ViewModel() {
    val userProfile: StateFlow<UserEntity?> = userRepository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val purchases = orderRepository.localPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentals = orderRepository.localRentals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateProfile(updatedUser: UserEntity) { // Accetta Entity!
        viewModelScope.launch {
            userRepository.updateProfile(userId, updatedUser)
        }
    }

    fun performFullLogout() {
        viewModelScope.launch {
            orderRepository.clearOrderHistory()
            inventoryRepository.clearCache()
        }
    }
}