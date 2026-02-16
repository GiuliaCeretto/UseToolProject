package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LinkingViewModel : ViewModel() {

    private val correctCode = "2568"

    private val _inputCode = MutableStateFlow("")
    val inputCode: StateFlow<String> = _inputCode

    private val _isLinked = MutableStateFlow(false)
    val isLinked: StateFlow<Boolean> = _isLinked

    fun addDigit(digit: Int) {
        if (_inputCode.value.length < correctCode.length) {
            _inputCode.value += digit.toString()
        }
    }

    fun resetCode() {
        _inputCode.value = ""
    }

    fun checkCode(): Boolean {
        return _inputCode.value == correctCode
    }

    fun link() {
        _isLinked.value = true
    }
}


