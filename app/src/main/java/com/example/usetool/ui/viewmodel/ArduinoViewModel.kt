package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.ArduinoEntity
import com.example.usetool.data.repository.ArduinoRepository
import com.example.usetool.data.service.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArduinoViewModel(
    private val arduinoRepository: ArduinoRepository = Injection.provideArduinoRepository()
) : ViewModel() {

    /**
     * Espone lo stato dell'hardware (connessione e porte) leggendo dal DB locale Room.
     * Si aggiorna automaticamente grazie al Flow della Repository.
     */
    val arduinoStatus: StateFlow<ArduinoEntity?> = arduinoRepository.arduinoStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Stato interno per gestire il caricamento durante l'apertura fisica
    private val _isOpening = MutableStateFlow(false)
    val isOpening: StateFlow<Boolean> = _isOpening.asStateFlow()

    // Stato per gestire eventuali messaggi di errore o successo
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        // Sincronizza lo stato attuale da Firebase a Room all'inizializzazione
        refreshHardwareStatus()
    }

    /**
     * Forza la sincronizzazione dei dati tra Firebase e il database locale Room.
     */
    fun refreshHardwareStatus() {
        viewModelScope.launch {
            try {
                arduinoRepository.syncStatusFromRemote()
            } catch (e: Exception) {
                _uiMessage.value = "Errore durante la sincronizzazione hardware"
            }
        }
    }

    /**
     * Esegue il comando di apertura fisica dello sportello.
     * @param isRental Se true, apre 'openRentDoor', altrimenti apre 'openBuyDoor'.
     */
    fun triggerDoor(isRental: Boolean) {
        // Evita clic multipli se un'apertura è già in corso (impulso di 3 secondi)
        if (_isOpening.value) return

        viewModelScope.launch {
            _isOpening.value = true
            _uiMessage.value = "Apertura locker in corso..."
            try {
                // Chiama la logica della repository: setValue(true) -> delay(3000) -> setValue(false)
                arduinoRepository.openDoor(isRental)
                _uiMessage.value = "Locker aperto! Puoi ritirare lo strumento"
            } catch (e: Exception) {
                _uiMessage.value = "Errore critico: Impossibile comunicare con il locker"
            } finally {
                _isOpening.value = false
                // Reset del messaggio dopo un breve periodo
                launch {
                    kotlinx.coroutines.delay(4000)
                    _uiMessage.value = null
                }
            }
        }
    }
}