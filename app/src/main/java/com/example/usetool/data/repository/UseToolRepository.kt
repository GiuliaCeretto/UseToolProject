package com.example.usetool.data.repository

import com.example.usetool.data.dto.ExpertDTO
import com.example.usetool.data.dto.LockerDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.flow.Flow

class UseToolRepository(private val firebaseService: DataSource) {

    val tools: Flow<List<ToolDTO>> = firebaseService.observeTools()

    val lockers: Flow<List<LockerDTO>> = firebaseService.observeLockers()

    val experts: Flow<List<ExpertDTO>> = firebaseService.observeExperts()
}