package com.example.usetool.data.dto

data class ToolDTO(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageResName: String = "placeholder_tool",
    val imageUrl: String? = null, // Aggiunto per immagini da Firebase Storage
    val videoUrl: String = "",
    val pdfUrls: List<String> = emptyList(),
    val category: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val type: String = "noleggio" // "noleggio" o "acquisto"
)