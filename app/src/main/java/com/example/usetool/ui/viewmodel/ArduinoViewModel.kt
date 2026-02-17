package com.example.usetool.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dao.ArduinoEntity
import com.example.usetool.data.repository.ArduinoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArduinoViewModel(
    private val repository: ArduinoRepository
) : ViewModel() {

    /**
     * Trasforma il Flow della Repo in uno StateFlow.
     * La UI osserverà questo oggetto per aggiornarsi automaticamente.
     */
    val arduinoState: StateFlow<ArduinoEntity?> = repository.getLocalStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        // 🔥 FONDAMENTALE: Avvia l'ascolto dei dati da Firebase verso Room
        viewModelScope.launch {
            repository.startSync()
        }
    }

    /**
     * Azione per aprire la porta di noleggio (Rental)
     */
    fun onOpenDoorClicked() {
        viewModelScope.launch {
            repository.openDoor()
        }
    }

    /**
     * Azione per l'erogazione dell'oggetto (Purchase)
     */
    fun onGiveObjClicked() {
        viewModelScope.launch {
            repository.giveObj()
        }
    }

    /**
     * Reset manuale degli stati (chiude virtualmente le porte)
     */
    fun onResetRequested() {
        viewModelScope.launch {
            repository.resetAllDoors()
        }
    }
}