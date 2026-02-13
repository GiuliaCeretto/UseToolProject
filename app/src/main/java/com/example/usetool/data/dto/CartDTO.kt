package com.example.usetool.data.dto

data class CartDTO(
    val id: String? = null,
    val userId: String? = null,
    val items: Map<String, SlotDTO> = emptyMap(),
    val totaleProvvisorio: Double = 0.0,
    val status: String = "PENDING",
    val ultimoAggiornamento: Long = System.currentTimeMillis()
)