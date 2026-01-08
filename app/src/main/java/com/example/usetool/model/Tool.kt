package com.example.usetool.model

data class Tool(
    val id: String,
    val name: String,
    val shortDescription: String = "",
    val fullDescription: String = "",
    val imageRes: Int,                 // drawable
    val available: Boolean = true,

    // Prezzi
    val pricePerHour: Double? = null,  // se noleggiabile
    val purchasePrice: Double? = null, // se acquistabile

    // Dati tecnici dinamici
    val technicalData: Map<String, String> = emptyMap(),

    // Link esterni
    val pdfUrl: String? = null,
    val videoUrl: String? = null
)
