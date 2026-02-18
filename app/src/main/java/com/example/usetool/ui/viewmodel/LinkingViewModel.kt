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

    private val _selectedLockerLinkId = MutableStateFlow<Int?>(null)
    val selectedLockerLinkId: StateFlow<Int?> = _selectedLockerLinkId.asStateFlow()

    private val _availableLockers = MutableStateFlow<List<LockerEntity>>(emptyList())
    val availableLockers: StateFlow<List<LockerEntity>> = _availableLockers.asStateFlow()

    private val _showKeypad = MutableStateFlow(false)
    val showKeypad: StateFlow<Boolean> = _showKeypad.asStateFlow()

    private val _inputCode = MutableStateFlow("")
    val inputCode: StateFlow<String> = _inputCode.asStateFlow()

    // --- LOGICA REATTIVA DI FIREBASE ---
    // Questa variabile osserva il DB. Se isConnected diventa true a mano nel DB,
    // questo Flow emette immediatamente 'true'.
    val isHardwareConnected: StateFlow<Boolean> = arduinoRepository.observeArduinoState()
        .map { it?.isConnected ?: false }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val connectedLockerName: StateFlow<String?> = combine(isHardwareConnected, availableLockers) { connected, lockers ->
        if (connected) {
            lockers.find { it.linkId == _selectedLockerLinkId.value }?.name ?: "Locker"
        } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            inventoryRepository.allLockers.collectLatest { _availableLockers.value = it }
        }
    }

    fun initLinking(lockerIdsFromCart: List<Int>) {
        if (lockerIdsFromCart.isNotEmpty()) {
            _selectedLockerLinkId.value = lockerIdsFromCart.first()
        }
    }

    fun selectLocker(locker: LockerEntity) {
        _selectedLockerLinkId.value = locker.linkId
        _showKeypad.value = true
    }

    fun addDigit(digit: Int) {
        if (_inputCode.value.length < 5) _inputCode.value += digit.toString()
    }

    fun removeLastDigit() {
        if (_inputCode.value.isNotEmpty()) _inputCode.value = _inputCode.value.dropLast(1)
    }

    fun cancelKeypad() {
        _showKeypad.value = false
        _inputCode.value = ""
    }

    fun disconnectLocker() {
        viewModelScope.launch {
            arduinoRepository.terminateHandshake() // Porta isConnected a false su Firebase
            _showKeypad.value = false
            _inputCode.value = ""
        }
    }
}