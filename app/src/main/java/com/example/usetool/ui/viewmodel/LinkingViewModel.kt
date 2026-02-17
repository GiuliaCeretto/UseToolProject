package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.repository.*
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LinkingViewModel(
    private val linkRepo: LinkRepo = Injection.provideLinkRepo(),
    private val userRepository: UserRepository = Injection.provideUserRepository(),
    private val inventoryRepository: InventoryRepository = Injection.provideInventoryRepository(),
    private val arduinoRepository: ArduinoRepository = Injection.provideArduinoRepository()
) : ViewModel() {

    // --- STATI TASTIERINO ---
    private val _inputCode = MutableStateFlow("")
    val inputCode: StateFlow<String> = _inputCode.asStateFlow()

    private val _selectedLockerLinkId = MutableStateFlow<Int?>(null)
    val selectedLockerLinkId: StateFlow<Int?> = _selectedLockerLinkId.asStateFlow()

    private val _selectedLockerId = MutableStateFlow<String?>(null)
    val selectedLockerId: StateFlow<String?> = _selectedLockerId.asStateFlow()

    private val _availableLockers = MutableStateFlow<List<LockerEntity>>(emptyList())
    val availableLockers: StateFlow<List<LockerEntity>> = _availableLockers.asStateFlow()

    // --- LOGICA DI COLLEGAMENTO ---
    // Osserva il DB locale per vedere se c'è una connessione attiva
    val lastConnection = linkRepo.allLogs
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Indica se l'utente è attualmente collegato a un locker
    val isLinked: StateFlow<Boolean> = lastConnection
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Nome del locker connesso per la UI
    val connectedLockerName: StateFlow<String?> = combine(lastConnection, availableLockers) { conn, lockers ->
        if (conn != null) {
            lockers.find { it.linkId == conn.lockerId }?.name ?: "Locker #${conn.lockerId}"
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Carica la lista dei locker disponibili all'avvio
        viewModelScope.launch {
            inventoryRepository.allLockers.collectLatest { lockers ->
                _availableLockers.value = lockers
            }
        }
    }

    /**
     * Inizializza il processo di linking partendo dai locker suggeriti nel carrello
     */
    fun initLinking(lockerIdsFromCart: List<Int>) {
        if (lockerIdsFromCart.isNotEmpty()) {
            val targetLinkId = lockerIdsFromCart.first()
            _selectedLockerLinkId.value = targetLinkId
            val associatedLocker = _availableLockers.value.find { it.linkId == targetLinkId }
            _selectedLockerId.value = associatedLocker?.id
        }
    }

    fun selectLocker(locker: LockerEntity) {
        _selectedLockerId.value = locker.id
        _selectedLockerLinkId.value = locker.linkId
        _inputCode.value = ""
    }

    // --- LOGICA PIN ---
    fun addDigit(digit: Int) {
        if (_inputCode.value.length < 4) {
            _inputCode.value += digit.toString()
            if (_inputCode.value.length == 4) {
                checkPin()
            }
        }
    }

    fun removeLastDigit() {
        if (_inputCode.value.isNotEmpty()) {
            _inputCode.value = _inputCode.value.dropLast(1)
        }
    }

    private fun checkPin() {
        // Logica PIN: il PIN è uguale al LinkID del locker (es: Locker #1 -> PIN 1)
        // Per testare con 4 cifre puoi usare: .repeat(4).take(4)
        val correctPin = _selectedLockerLinkId.value?.toString()

        if (correctPin != null && _inputCode.value == correctPin) {
            confirmLink()
        } else {
            // Reset se sbagliato
            _inputCode.value = ""
        }
    }

    /**
     * 🔥 CONFERMA COLLEGAMENTO
     * Esegue l'handshake con l'hardware e salva la sessione locale.
     */
    private fun confirmLink() {
        val linkId = _selectedLockerLinkId.value ?: return
        val userId = userRepository.getCurrentUserId()

        viewModelScope.launch {
            try {
                // 1. Registra la sessione nel database locale
                linkRepo.logConnection(linkId, userId.hashCode())

                // 2. ⚡ HANDSHAKE HARDWARE: Modifica il nodo arduino esistente
                // Setta isConnected = true e resetta le porte a false
                arduinoRepository.initializeHardwareStatus()

            } catch (e: Exception) {
                _inputCode.value = ""
            }
        }
    }

    // In LinkingViewModel.kt

    fun resetSelection() {
        viewModelScope.launch {
            try {
                arduinoRepository.terminateHandshake()

                linkRepo.clearHistory()

                // 3. Reset variabili interne del tastierino
                _selectedLockerId.value = null
                _selectedLockerLinkId.value = null
                _inputCode.value = ""
            } catch (e: Exception) {
                // In caso di errore di rete, forza comunque il reset locale
                linkRepo.clearHistory()
            }
        }
    }
}