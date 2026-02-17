package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.repository.InventoryRepository
import com.example.usetool.data.repository.LinkRepo
import com.example.usetool.data.repository.UserRepository
import com.example.usetool.data.dao.LockerEntity
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LinkingViewModel(
    private val linkRepo: LinkRepo = Injection.provideLinkRepo(),
    private val userRepository: UserRepository = Injection.provideUserRepository(),
    private val inventoryRepository: InventoryRepository = Injection.provideInventoryRepository()
) : ViewModel() {

    private val _inputCode = MutableStateFlow("")
    val inputCode: StateFlow<String> = _inputCode.asStateFlow()

    private val _selectedLockerLinkId = MutableStateFlow<Int?>(null)
    val selectedLockerLinkId: StateFlow<Int?> = _selectedLockerLinkId.asStateFlow()

    private val _selectedLockerId = MutableStateFlow<String?>(null)
    val selectedLockerId: StateFlow<String?> = _selectedLockerId.asStateFlow()

    private val _availableLockers = MutableStateFlow<List<LockerEntity>>(emptyList())
    val availableLockers: StateFlow<List<LockerEntity>> = _availableLockers.asStateFlow()

    // 🔥 PERSISTENZA: Osserviamo l'ultimo log nel DB per determinare se siamo collegati
    val lastConnection = linkRepo.allLogs
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isLinked: StateFlow<Boolean> = lastConnection
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Recupera il nome del locker a cui sei collegato
    val connectedLockerName: StateFlow<String?> = combine(lastConnection, availableLockers) { conn, lockers ->
        if (conn != null) {
            lockers.find { it.linkId == conn.lockerId }?.name ?: "Locker #${conn.lockerId}"
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            inventoryRepository.allLockers.collectLatest { lockers ->
                _availableLockers.value = lockers
            }
        }
    }

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
        val correctPin = _selectedLockerLinkId.value?.toString()
        if (correctPin != null && _inputCode.value == correctPin) {
            confirmLink()
        } else {
            _inputCode.value = ""
        }
    }

    private fun confirmLink() {
        val linkId = _selectedLockerLinkId.value ?: return
        val userId = userRepository.getCurrentUserId()

        viewModelScope.launch {
            try {
                // 🔥 SALVATAGGIO REALE: Scrive nel DB locale tramite LinkRepo
                linkRepo.logConnection(linkId, userId.hashCode())
            } catch (e: Exception) {
                _inputCode.value = ""
            }
        }
    }

    fun resetSelection() {
        viewModelScope.launch {
            linkRepo.clearHistory() // 🔥 Rimuove il collegamento dal DB
            _selectedLockerId.value = null
            _selectedLockerLinkId.value = null
            _inputCode.value = ""
        }
    }
}