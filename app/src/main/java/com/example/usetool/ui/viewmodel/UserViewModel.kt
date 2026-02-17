package com.example.usetool.ui.viewmodel

import android.util.Log
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

    // --- DATI REATTIVI (ROOM) ---
    // Usiamo WhileSubscribed(5000) per mantenere il flusso attivo durante le rotazioni o piccoli cambi di schermo

    val userProfile: StateFlow<UserEntity?> = userRepository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val purchases: StateFlow<List<PurchaseEntity>> = orderRepository.localPurchases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentals: StateFlow<List<RentalEntity>> = orderRepository.localRentals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- FLOWS FILTRATI ---

    val activeRentals: StateFlow<List<RentalEntity>> = rentals
        .map { list -> list.filter { it.statoNoleggio == "ATTIVO" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val purchaseCount: StateFlow<Int> = purchases
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val rentalCount: StateFlow<Int> = rentals
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    init {
        // 🔥 FIX: Verifica se l'utente è già loggato all'avvio del ViewModel
        // Se l'utente ha una sessione attiva, avviamo subito la sincronizzazione.
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            syncUserData(uid)
        }
    }

    // --- LOGICA DI AUTENTICAZIONE ---

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginResult.Error("Inserisci email e password")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginResult.Loading
            try {
                userRepository.loginWithFirebase(email, pass).fold(
                    onSuccess = { handleAuthResult() },
                    onFailure = { e ->
                        _loginState.value = LoginResult.Error(e.localizedMessage ?: "Credenziali errate")
                    }
                )
            } catch (e: Exception) {
                _loginState.value = LoginResult.Error("Errore di rete")
            }
        }
    }

    private fun handleAuthResult() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            syncUserData(userId)
            _loginState.value = LoginResult.SuccessLogin
        } else {
            _loginState.value = LoginResult.Error("Utente non trovato")
        }
    }

    fun register(nome: String, cognome: String, email: String, telefono: String, indirizzo: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading
            userRepository.registerWithFirebase(nome, cognome, email, telefono, indirizzo, pass).fold(
                onSuccess = { _loginState.value = LoginResult.SuccessRegister },
                onFailure = { e -> _loginState.value = LoginResult.Error(e.localizedMessage ?: "Errore") }
            )
        }
    }

    // --- SINCRONIZZAZIONE ---

    private fun syncUserData(userId: String) {
        viewModelScope.launch {
            try {
                // Avviamo i processi di sync nel Repository
                userRepository.syncProfile(userId)
                orderRepository.syncUserOrders(userId)
                Log.d("UserViewModel", "Sincronizzazione ordini avviata per: $userId")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Errore sync: ${e.message}")
            }
        }
    }

    // --- AZIONI ORDINI ---

    fun startRental(rentalId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                orderRepository.confirmStartRental(uid, rentalId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore avvio noleggio")
            }
        }
    }

    fun endRental(rentalId: String, slotId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                orderRepository.confirmEndRental(uid, rentalId, slotId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore riconsegna")
            }
        }
    }

    fun confirmPickup(purchaseId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                orderRepository.confirmPurchasePickup(uid, purchaseId)
            } catch (e: Exception) {
                _errorMessage.emit("Errore ritiro")
            }
        }
    }

    // --- PROFILO E LOGOUT ---

    fun updateUserData(nome: String, cognome: String, telefono: String, indirizzo: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val updatedUser = UserEntity(userId, FirebaseAuth.getInstance().currentUser?.email ?: "", nome, cognome, telefono, indirizzo)
        viewModelScope.launch {
            try {
                userRepository.updateProfile(userId, updatedUser)
            } catch (e: Exception) {
                _errorMessage.emit("Errore aggiornamento profilo")
            }
        }
    }

    fun triggerError(message: String) {
        _loginState.value = LoginResult.Error(message)
        viewModelScope.launch {
            _errorMessage.emit(message)
        }
    }

    fun performFullLogout() {
        viewModelScope.launch {
            try {
                // 1. Pulizia dati (Repository e Firebase)
                userRepository.logout()
                orderRepository.clearOrderHistory()
                inventoryRepository.clearCache()

                // 2. Reset dello stato UI
                _loginState.value = LoginResult.Idle

                // 3. Lancio dell'evento di navigazione
                _logoutEvent.emit(Unit)

                Log.d("UserViewModel", "Logout eseguito correttamente")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Errore durante il logout", e)
            }
        }
    }

    fun resetState() { _loginState.value = LoginResult.Idle }

    // --- CAMPI LOGIN PERSISTENTI ---

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }
}

sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    object SuccessLogin : LoginResult()
    object SuccessRegister : LoginResult()
    data class Error(val message: String) : LoginResult()
}