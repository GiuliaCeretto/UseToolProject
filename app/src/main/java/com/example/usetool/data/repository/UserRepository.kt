package com.example.usetool.data.repository

import com.example.usetool.data.dao.UserDao
import com.example.usetool.data.dao.UserEntity
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toDto
import com.example.usetool.data.service.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class UserRepository(
    private val dataSource: DataSource,
    private val userDao: UserDao
) {
    // Espone solo Entity
    val userProfile: Flow<UserEntity?> = userDao.getProfile()

    suspend fun syncProfile(userId: String) {
        dataSource.observeProfile(userId).collectLatest { dto ->
            dto?.let {
                // Converte DTO -> Entity prima di salvare in Room
                userDao.saveProfile(it.toEntity())
            }
        }
    }

    // Ora accetta solo UserEntity
    suspend fun updateProfile(userId: String, updatedUser: UserEntity) {
        // Converte Entity -> DTO internamente per il database remoto
        val userDto = updatedUser.toDto()

        val updates = mapOf(
            "users/$userId" to userDto
        )
        dataSource.updateMultipleNodes(updates)

        // Salva l'Entity direttamente in locale
        userDao.saveProfile(updatedUser)
    }
}