package com.example.usetool.data.repository

import com.example.usetool.data.dto.ExpertDTO
import com.example.usetool.data.dto.LockerDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.services.FirebaseService
import kotlinx.coroutines.flow.Flow

class UseToolRepository(private val firebaseService: FirebaseService) {

    val tools: Flow<List<ToolDTO>> = firebaseService.observeTools()

    val lockers: Flow<List<LockerDTO>> = firebaseService.observeLockers()

    val experts: Flow<List<ExpertDTO>> = firebaseService.observeExperts()
}