package com.example.usetool.data.dto

data class RentalDTO(
    val id: String? = null,
    val userId: String? = null,
    val toolId: String? = null,
    val toolName: String? = null,
    val lockerId: String? = null,
    val slotId: String? = null,
    val dataInizio: Long = System.currentTimeMillis(),
    val dataFinePrevista: Long? = null,
    val dataRiconsegnaEffettiva: Long? = null,
    val statoNoleggio: String = "ATTIVO", // "ATTIVO", "COMPLETATO", "IN_RITARDO"
    val costoTotale: Double = 0.0
)