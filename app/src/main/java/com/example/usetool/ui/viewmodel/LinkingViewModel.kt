package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LinkingViewModel : ViewModel() {
    private val _isLinked = MutableStateFlow(false)
    val isLinked: StateFlow<Boolean> = _isLinked

    fun link() {
        // simulate pairing
        _isLinked.value = true
    }

    fun unlink() {
        _isLinked.value = false
    }
}

