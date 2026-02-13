package com.example.usetool.data.repository

import com.example.usetool.data.dao.ExpertDao
import com.example.usetool.data.dao.ExpertEntity
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.service.toEntityList // Importa la versione per liste
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

class ExpertRepository(
    private val dataSource: DataSource,
    private val expertDao: ExpertDao
) {
    // Espone i dati da Room
    val experts: Flow<List<ExpertEntity>> = expertDao.getAll()

    // Sincronizza usando la funzione di estensione per liste
    suspend fun syncExperts() {
        dataSource.observeExperts().collectLatest { dtos ->
            // Ora passiamo direttamente la lista mappata
            expertDao.insertAll(dtos.toEntityList())
        }
    }
}