package com.example.usetool.data.dto

data class CartDTO(
    val id: String? = null,
    val userId: String? = null,
    val items: Map<String, SlotDTO> = emptyMap(), // ID Cassetto -> Dettagli rapidi
    val totaleProvvisorio: Double = 0.0,
    val ultimoAggiornamento: Long = System.currentTimeMillis()
)