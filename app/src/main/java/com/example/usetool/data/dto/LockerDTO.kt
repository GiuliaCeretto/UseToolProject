package com.example.usetool.data.dto

data class LockerDTO(
    val id: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val city: String = "",
    val address: String = "",
    val zipCode: String = "",
    val saleSlotsCount: Int = 0,
    val rentalSlotsCount: Int = 0,
    val macAddress: String = "",
    val toolIds: List<String> = emptyList(),
    val linkId: Int = 0
    )