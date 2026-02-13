package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.UserDTO
import com.example.usetool.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val userId = "demo_user_id"

    val userProfile: StateFlow<UserDTO?> = userRepository.getUserProfile(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateProfile(updatedUser: UserDTO) {
        viewModelScope.launch {
            userRepository.updateProfile(userId, updatedUser)
        }
    }
}