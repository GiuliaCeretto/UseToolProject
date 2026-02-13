package com.example.usetool.data.repository

import com.example.usetool.data.dto.UserDTO
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.UseToolService
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val dataSource: DataSource,
    private val uTservice: UseToolService
) {
    // Osserva i dati del profilo dell'utente (nome, cognome, indirizzo, etc.)
    fun getUserProfile(userId: String): Flow<UserDTO?> = dataSource.observeProfile(userId)

    // Aggiorna le informazioni del profilo in modo atomico
    suspend fun updateProfile(userId: String, updatedUser: UserDTO) {
        val updates = mapOf(
            "users/$userId" to updatedUser
        )
        uTservice.updateMultipleNodes(updates)
    }
}