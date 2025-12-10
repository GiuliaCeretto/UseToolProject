package com.example.usetool.model

data class Distributor(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val toolsAvailable: List<String> = emptyList()
)
