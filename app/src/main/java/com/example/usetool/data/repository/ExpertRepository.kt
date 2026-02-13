package com.example.usetool.data.repository

import com.example.usetool.data.dto.ExpertDTO
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.flow.Flow

class ExpertRepository(private val dataSource: DataSource) {

    val experts: Flow<List<ExpertDTO>> = dataSource.observeExperts()

}