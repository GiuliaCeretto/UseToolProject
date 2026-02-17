package com.example.usetool.data.dto

data class ArduinoStateDto(
    val isConnected: Boolean = false,
    val openBuyDoor: Boolean = false,
    val openRentDoor: Boolean = false
)