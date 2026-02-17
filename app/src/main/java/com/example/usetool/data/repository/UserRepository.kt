package com.example.usetool.data.repository

import com.example.usetool.data.dao.UserDao
import com.example.usetool.data.dao.UserEntity
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toDto
import com.example.usetool.data.service.toEntity
import com.google.firebase.auth.FirebaseAuth // Import necessario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await // Necessario per usare .await()

class UserRepository(
    private val dataSource: DataSource,
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth // Aggiunto al costruttore
) {
    // Espone solo Entity per la UI
    val userProfile: Flow<UserEntity?> = userDao.getProfile()

    fun getCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid ?: ""
    }

    suspend fun loginWithFirebase(email: String, pass: String): Result<Unit> {
        return try {
            // Ora firebaseAuth è accessibile
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncProfile(userId: String) {
        dataSource.observeProfile(userId).collectLatest { dto ->
            dto?.let {
                userDao.saveProfile(it.toEntity())
            }
        }
    }

    suspend fun updateProfile(userId: String, updatedUser: UserEntity) {
        val userDto = updatedUser.toDto()
        val updates = mapOf(
            "users/$userId" to userDto
        )
        dataSource.updateMultipleNodes(updates)
        userDao.saveProfile(updatedUser)
    }

    /* UserRepository.kt */
    suspend fun registerWithFirebase(
        nome: String,
        cognome: String,
        email: String,
        telefono: String,
        indirizzo: String,
        pass: String
    ): Result<Unit> {
        return try {
            // 1. Creazione account su Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("ID utente non generato")

            // 2. Creazione dell'entità
            val newUser = UserEntity(
                uid = firebaseUid,
                nome = nome,
                cognome = cognome,
                telefono = telefono,
                indirizzo = indirizzo,
                email = email
            )

            // 3. Salvataggio remoto
            // Importante: updateProfile invia i dati al database.
            // Se questo fallisce, l'utente Auth esiste ma il profilo no.
            updateProfile(firebaseUid, newUser)

            Result.success(Unit)
        } catch (e: Exception) {
            // Se fallisce la scrittura sul DB, potresti voler eliminare l'utente Auth appena creato
            // per permettere di riprovare con la stessa email.
            firebaseAuth.currentUser?.delete()?.await()
            Result.failure(e)
        }
    }
    suspend fun logout() {
        firebaseAuth.signOut()
        userDao.clearProfile() // Pulisce Room
    }
}