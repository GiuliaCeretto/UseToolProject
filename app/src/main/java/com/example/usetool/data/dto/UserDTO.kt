package com.example.usetool.data.dto

data class UserDTO(
    val email: String? = null,
    val passwordHash: String? = null,
    val passwordSalt: String? = null,
    val nome: String? = null,
    val cognome: String? = null,
    val telefono: String? = null,
    val indirizzo: String? = null
)