package com.example.usetool.data.dto

data class SlotDTO(
    val id: String = "",
    val lockerId: String = "",
    val toolId: String = "",
    val status: String = "VUOTO", // VUOTO, DISPONIBILE, IN_CARRELLO, NOLEGGIATO, DANNEGGIATO
    val quantity: Int = 0
)