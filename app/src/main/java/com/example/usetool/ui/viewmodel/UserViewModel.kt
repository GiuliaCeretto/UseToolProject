package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.User

class UserViewModel : ViewModel() {
    private val _user = MutableStateFlow(User("u1","Demo User", 50.0))
    val user: StateFlow<User> = _user
}
