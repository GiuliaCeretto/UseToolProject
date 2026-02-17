package com.example.usetool.data.repository

import com.example.usetool.data.dao.ArduinoDao
import com.example.usetool.data.dao.ArduinoEntity
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.network.FirebaseProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ArduinoRepository(
    private val arduinoDao: ArduinoDao,
    private val dataSource: DataSource,      // Utilizzato per osservare i dati (Flow)
    private val firebase: FirebaseProvider    // Utilizzato per scrivere i comandi (setValue)
) {
    /**
     * Espone lo stato hardware salvato localmente nel database Room.
     */
    val arduinoStatus: Flow<ArduinoEntity?> = arduinoDao.getArduinoStatus()

    /**
     * Sincronizza lo stato da Firebase a Room.
     * Questa è la funzione che mancava nel tuo frammento di codice.
     */
    suspend fun syncStatusFromRemote() {
        // Recupera l'ultimo valore emesso dal Flow nel DataSource
        val remoteState = dataSource.observeArduinoState().firstOrNull()

        if (remoteState != null) {
            // Converte il DTO di Firebase nell'Entity di Room
            val entity = ArduinoEntity(
                id = "current_status", // ID fisso per mantenere un'unica riga nel DB
                isConnected = remoteState.isConnected,
                openBuyDoor = remoteState.openBuyDoor,
                openRentDoor = remoteState.openRentDoor,
                lastSync = System.currentTimeMillis()
            )
            // Inserisce o aggiorna nel database locale
            arduinoDao.insertStatus(entity)
        }
    }

    /**
     * Gestisce l'apertura fisica inviando comandi al FirebaseProvider.
     * Include un impulso di 3 secondi prima di resettare lo stato a false.
     */
    suspend fun openDoor(isRental: Boolean) {
        val nodeName = if (isRental) "openRentDoor" else "openBuyDoor"
        val doorRef = firebase.getArduinoRef().child(nodeName)

        try {
            // 1. Invia comando di apertura (true)
            doorRef.setValue(true)

            // 2. Attesa impulso hardware (3 secondi) per permettere lo sblocco
            delay(3000)

            // 3. Reset dello stato (false) per non bruciare il solenoide/relè
            doorRef.setValue(false)

            // 4. Sincronizza il database locale per riflettere il cambio di stato
            syncStatusFromRemote()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}