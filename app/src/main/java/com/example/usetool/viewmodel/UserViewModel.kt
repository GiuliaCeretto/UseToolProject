package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.User

class UserViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    val isLogged: Boolean
        get() = _user.value != null

    fun loginFake(email: String) {
        _user.value = User(
            id = "u1",
            name = email.substringBefore("@"),
            balance = 50.0
        )
    }

    fun logout() {
        _user.value = null
    }
}

