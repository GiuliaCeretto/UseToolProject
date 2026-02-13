package com.example.usetool.data.repository

import com.example.usetool.data.dao.UserDao
import com.example.usetool.data.dao.UserEntity
import com.example.usetool.data.dto.UserDTO
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class UserRepository(
    private val dataSource: DataSource,
    private val userDao: UserDao, // Iniettato per risolvere i warning del DAO
) {
    // RISOLTO: Ora getProfile() è usato per esporre i dati alla UI
    // Questo garantisce che l'utente veda il profilo anche offline
    val userProfile: Flow<UserEntity?> = userDao.getProfile()

    /**
     * Sincronizza i dati del profilo da Firebase a Room.
     * RISOLTO: Utilizza saveProfile() del DAO e toEntity() del Mapper.
     */
    suspend fun syncProfile(userId: String) {
        dataSource.observeProfile(userId).collectLatest { dto ->
            dto?.let {
                userDao.saveProfile(it.toEntity())
            }
        }
    }

    // Aggiorna Firebase e, se l'operazione ha successo, aggiorna anche Room
    suspend fun updateProfile(userId: String, updatedUser: UserDTO) {
        val updates = mapOf(
            "users/$userId" to updatedUser
        )
        dataSource.updateMultipleNodes(updates)

        // Aggiornamento locale immediato per una UI reattiva
        userDao.saveProfile(updatedUser.toEntity())
    }
}