package com.example.usetool.data.dto

data class PurchaseDTO(
    val id: String? = null,
    val userId: String? = null,
    val cartId: String? = null, // Riferimento al carrello sorgente
    val toolId: String? = null,
    val toolName: String? = null,
    val prezzoPagato: Double = 0.0,
    val dataAcquisto: Long = System.currentTimeMillis(),
    val dataRitiroEffettiva: Long? = null,
    val lockerId: String? = null,
    val slotId: String? = null,
    val idTransazionePagamento: String? = null, // ID finto del gateway di pagamento
    val dataRitiro: Long? = null
)