package com.example.usetool.data.repository

import com.example.usetool.data.dao.ArduinoDao
import com.example.usetool.data.dao.ArduinoEntity
import com.example.usetool.data.dto.ArduinoStateDto
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class ArduinoRepository(
    private val dataSource: DataSource,
    private val arduinoDao: ArduinoDao
) {

    // Espone lo stato locale alla UI
    fun getLocalStatus(): Flow<ArduinoEntity?> = arduinoDao.getArduinoStatus()

    /**
     * Sincronizza Firebase con Room.
     * Quando i flag su Firebase cambiano, aggiorna il database locale.
     */
    suspend fun startSync() {
        dataSource.observeArduino().collectLatest { dto ->
            if (dto == null) return@collectLatest

            // Trasforma il DTO di rete nell'Entity per il DB locale
            val entity = ArduinoEntity(
                isConnected = dto.isConnected ?: false,
                openBuyDoor = dto.openBuyDoor ?: false,
                openRentDoor = dto.openRentDoor ?: false,
                lastSync = System.currentTimeMillis()
            )

            arduinoDao.insertStatus(entity)
        }
    }

    /**
     * Logica "OpenDoor": Imposta la flag per il noleggio (Rent) su Firebase.
     */
    suspend fun openDoor() {
        dataSource.updateNode("arduino/openRentDoor", true)
    }

    /**
     * Logica "GiveObj": Imposta la flag per l'acquisto (Buy) su Firebase.
     */
    suspend fun giveObj() {
        dataSource.updateNode("arduino/openBuyDoor", true)
    }

    /**
     * Reset manuale: riporta tutte le porte a stato chiuso su Firebase.
     */
    suspend fun resetAllDoors() {
        val updates = mapOf(
            "arduino/openRentDoor" to false,
            "arduino/openBuyDoor" to false
        )
        dataSource.updateMultipleNodes(updates)
    }
}