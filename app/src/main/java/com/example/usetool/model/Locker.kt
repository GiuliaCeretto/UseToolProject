package com.example.usetool.model

data class Locker(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val toolsAvailable: List<String> = emptyList(),
    val distanceKm: Double = 1.2 // mock
)
