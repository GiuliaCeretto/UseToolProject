package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.UserEntity
import com.example.usetool.data.dto.UserDTO
import com.example.usetool.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Modifica in UserViewModel.kt
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val userId = "demo_user_id"

    // RISOLTO: Usa direttamente la proprietà 'userProfile' definita nel Repository
    val userProfile: StateFlow<UserEntity?> = userRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        // Avvia la sincronizzazione da Firebase all'avvio
        viewModelScope.launch {
            userRepository.syncProfile(userId)
        }
    }

    fun updateProfile(updatedUser: UserDTO) {
        viewModelScope.launch {
            userRepository.updateProfile(userId, updatedUser)
        }
    }
}