package com.example.usetool.data.repository

import com.example.usetool.data.dto.*
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val dataSource: DataSource) {
    // Osserva il catalogo attrezzi
    val tools: Flow<List<ToolDTO>> = dataSource.observeTools()

    // Osserva la struttura fisica
    val lockers: Flow<List<LockerDTO>> = dataSource.observeLockers()
    val slots: Flow<List<SlotDTO>> = dataSource.observeSlots()
}