package com.example.usetool.data.repository

import com.example.usetool.data.dao.ArduinoDao
import com.example.usetool.data.dao.ArduinoEntity
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class ArduinoRepository(
    private val dataSource: DataSource,
    private val arduinoDao: ArduinoDao
) {

    /**
     * Espone lo stato locale salvato in Room.
     */
    fun getLocalStatus(): Flow<ArduinoEntity?> = arduinoDao.getArduinoStatus()

    /**
     * Ascolta Firebase e aggiorna Room.
     * Non crea nuovi nodi, legge solo quelli esistenti sotto "arduino".
     */
    suspend fun startSync() {
        dataSource.observeArduino().collectLatest { dto ->
            if (dto != null) {
                arduinoDao.insertStatus(
                    ArduinoEntity(
                        id = "current_status", // ID fisso per sovrascrivere sempre la stessa riga in Room
                        isConnected = dto.isConnected ?: false,
                        openBuyDoor = dto.openBuyDoor ?: false,
                        openRentDoor = dto.openRentDoor ?: false,
                        lastSync = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    /**
     * 🔥 INIZIALIZZAZIONE / HANDSHAKE
     * Questa funzione MODIFICA i valori esistenti su Firebase.
     * Utilizza updateChildren per sovrascrivere i parametri isConnected,
     * openBuyDoor e openRentDoor senza creare nuovi nodi.
     */
    suspend fun initializeHardwareStatus() {
        // La mappa delle chiavi deve corrispondere esattamente ai nomi su Firebase
        val updates = mapOf(
            "isConnected" to true,
            "openBuyDoor" to false,
            "openRentDoor" to false
        )

        // 1. Aggiorna Firebase puntando direttamente ai figli del nodo "arduino"
        dataSource.updateArduinoMultiple(updates)

        // 2. Aggiorna Room localmente per un feedback UI immediato
        arduinoDao.insertStatus(
            ArduinoEntity(
                id = "current_status",
                isConnected = true,
                openBuyDoor = false,
                openRentDoor = false,
                lastSync = System.currentTimeMillis()
            )
        )
    }

    /**
     * 🔥 MODIFICA: Apre la porta di noleggio sovrascrivendo il campo "openRentDoor"
     */
    suspend fun openDoor() {
        dataSource.updateArduinoNode("openRentDoor", true)
    }

    /**
     * 🔥 MODIFICA: Apre la porta di acquisto sovrascrivendo il campo "openBuyDoor"
     */
    suspend fun giveObj() {
        dataSource.updateArduinoNode("openBuyDoor", true)
    }

    /**
     * 🔥 MODIFICA: Resetta solo i flag delle porte sovrascrivendo i campi esistenti
     */
    suspend fun resetAllDoors() {
        val updates = mapOf(
            "openRentDoor" to false,
            "openBuyDoor" to false
        )
        dataSource.updateArduinoMultiple(updates)
    }

    suspend fun terminateHandshake() {
        val updates = mapOf(
            "isConnected" to false,    // 👈 Fondamentale: ora passerà a false su Firebase
            "openRentDoor" to false,
            "openBuyDoor" to false
        )

        // 1. Aggiorna Firebase (sovrascrive i campi esistenti)
        dataSource.updateArduinoMultiple(updates)

        // 2. Aggiorna Room localmente
        arduinoDao.insertStatus(
            ArduinoEntity(
                id = "current_status",
                isConnected = false,
                openBuyDoor = false,
                openRentDoor = false,
                lastSync = System.currentTimeMillis()
            )
        )
    }
}