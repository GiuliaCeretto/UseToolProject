package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ArduinoRepository(
    private val dataSource: DataSource,
    private val lockerDao: LockerDao,
    private val slotDao: SlotDao,
    private val commandDao: CommandDao // Dao ipotetico per i log dei comandi
) {
    // Evita loop di sincronizzazione durante l'invio di impulsi all'hardware
    private var isHardwareOperationActive = false

    /**
     * Osserva lo stato dei locker dal network e aggiorna il DB locale.
     * Utile per monitorare se un locker è online o se ci sono errori hardware.
     */
    suspend fun syncLockerStatus(buildingId: String) {
        dataSource.observeLockersByBuilding(buildingId).collectLatest { lockerDtos ->
            if (isHardwareOperationActive) return@collectLatest

            val entities = lockerDtos.map { dto ->
                LockerEntity(
                    id = dto.id,
                    linkId = dto.linkId, // ID numerico per la scheda Arduino
                    status = dto.status,
                    lastHeartbeat = System.currentTimeMillis()
                )
            }
            lockerDao.insertAll(entities)
        }
    }

    /**
     * Invia un comando di sblocco a uno slot specifico.
     * Segue la logica "Local-First": aggiorna Room, poi Firebase.
     */
    suspend fun unlockSlot(lockerId: String, slotIndex: Int) {
        isHardwareOperationActive = true
        try {
            // 1. Recuperiamo il riferimento locale del locker
            val locker = lockerDao.getLockerById(lockerId).firstOrNull() ?: return
            val arduinoId = locker.linkId // Il numero della scheda hardware

            // 2. Prepariamo l'update atomico per Firebase
            // Scriviamo in una coda di comandi che l'Arduino sta ascoltando
            val commandId = "cmd_${System.currentTimeMillis()}"
            val commandData = mapOf(
                "arduinoId" to arduinoId,
                "slotIndex" to slotIndex,
                "action" to "UNLOCK",
                "timestamp" to System.currentTimeMillis(),
                "status" to "PENDING"
            )

            val updates = mapOf(
                "commands/$commandId" to commandData,
                "slots/${lockerId}_$slotIndex/lastAction" to "UNLOCKING"
            )

            // 3. Esecuzione
            slotDao.updateSlotStatus("${lockerId}_$slotIndex", "SBLOCCO_IN_CORSO")
            dataSource.updateMultipleNodes(updates)

            // Simula un tempo di attesa per l'impulso fisico
            delay(1000)

        } catch (e: Exception) {
            // Gestione errore: ripristina lo stato precedente se il network fallisce
            slotDao.updateSlotStatus("${lockerId}_$slotIndex", "ERRORE_HARDWARE")
        } finally {
            isHardwareOperationActive = false
        }
    }

    /**
     * Reset hardware di un intero locker (tutti i pin di Arduino).
     */
    suspend fun resetLocker(lockerId: String) {
        isHardwareOperationActive = true
        try {
            val locker = lockerDao.getLockerById(lockerId).firstOrNull() ?: return

            val resetUpdate = mapOf(
                "hardware_control/${locker.linkId}/reset" to true,
                "lockers/$lockerId/status" to "RESETTING"
            )

            dataSource.updateMultipleNodes(resetUpdate)

            // Attendiamo che il reset venga propagato
            delay(2000)

            // Riportiamo il reset a false (edge-triggered)
            dataSource.updateNode("hardware_control/${locker.linkId}/reset", false)
        } finally {
            isHardwareOperationActive = false
        }
    }
}