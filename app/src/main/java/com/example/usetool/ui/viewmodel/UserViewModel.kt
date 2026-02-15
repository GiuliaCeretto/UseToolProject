package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.PurchaseEntity
import com.example.usetool.data.dao.RentalEntity
import com.example.usetool.data.dao.UserEntity
import com.example.usetool.data.repository.InventoryRepository
import com.example.usetool.data.repository.OrderRepository
import com.example.usetool.data.repository.UserRepository
import com.example.usetool.data.service.Injection
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository = Injection.provideUserRepository(),
    private val orderRepository: OrderRepository = Injection.provideOrderRepository(),
    private val inventoryRepository: InventoryRepository = Injection.provideInventoryRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginState: StateFlow<LoginResult> = _loginState.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    // --- DATI CARICATI PIGRAMENTE (LAZILY) ---

    val userProfile: StateFlow<UserEntity?> by lazy {
        userRepository.userProfile
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    val purchases: StateFlow<List<PurchaseEntity>> by lazy {
        orderRepository.localPurchases
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    val rentals: StateFlow<List<RentalEntity>> by lazy {
        orderRepository.localRentals
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    private fun syncUserData(userId: String) {
        viewModelScope.launch {
            // Chiama le funzioni del repository che prima risultavano "unused"
            userRepository.syncProfile(userId)
            orderRepository.syncUserOrders(userId)
        }
    }

    fun updateUserData(nome: String, cognome: String, telefono: String, indirizzo: String) {
        val currentProfile = userProfile.value ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updatedUser = currentProfile.copy(nome = nome, cognome = cognome, telefono = telefono, indirizzo = indirizzo)

        viewModelScope.launch {
            try {
                userRepository.updateProfile(userId, updatedUser)
            } catch (_: Exception) {
                // CORRETTO: Emette errore su SharedFlow senza resettare il login
                _errorMessage.emit("Errore durante l'aggiornamento del profilo")
            }
        }
    }

    // Aggiungi queste funzioni in UserViewModel.kt
    fun confirmPickup(purchaseId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                orderRepository.confirmPurchasePickup(uid, purchaseId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore nel confermare il ritiro")
            }
        }
    }

    fun startRental(rentalId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                orderRepository.confirmStartRental(uid, rentalId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore nell'avvio del noleggio")
            }
        }
    }

    fun endRental(rentalId: String, slotId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                orderRepository.confirmEndRental(uid, rentalId, slotId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore nella riconsegna")
            }
        }
    }

    // --- LOGICA DI LOGIN E SINCRONIZZAZIONE ---

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginResult.Error("Inserisci email e password")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginResult.Loading

            val result = userRepository.loginWithFirebase(email, pass)

            result.onSuccess {
                // Dopo il login, recuperiamo l'UID per far partire la sincronizzazione
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    // RISOLTO: Avviamo la sincronizzazione di profilo e ordini
                    syncUserData(userId)
                    _loginState.value = LoginResult.Success
                } else {
                    _loginState.value = LoginResult.Error("Errore nel recupero ID utente")
                }
            }.onFailure { exception ->
                _loginState.value = LoginResult.Error(exception.message ?: "Errore di autenticazione")
            }
        }
    }

    fun performFullLogout() {
        viewModelScope.launch {
            userRepository.logout()
            orderRepository.clearOrderHistory()
            inventoryRepository.clearCache()
            _loginState.value = LoginResult.Idle
        }
    }


}

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}