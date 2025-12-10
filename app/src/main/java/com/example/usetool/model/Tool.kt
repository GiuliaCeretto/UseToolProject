package com.example.usetool.model

data class Tool(
    val id: String,
    val name: String,
    val description: String = "",
    val imageUrl: String? = null,
    val available: Boolean = true,
    val pricePerHour: Double = 0.0
)
