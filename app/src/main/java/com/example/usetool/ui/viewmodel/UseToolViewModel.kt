package com.example.usetool.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.network.FirebaseDao
import com.example.usetool.data.services.FirebaseService
import com.example.usetool.data.repository.UseToolRepository
import com.example.usetool.model.Tool
import com.example.usetool.model.Locker
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.dto.LockerDTO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UseToolViewModel(context: Context) : ViewModel() {

    // 1. Inizializziamo i componenti della catena (DAO -> Service -> Repo)
    private val dao = FirebaseDao()
    private val service = FirebaseService(dao)
    private val repository = UseToolRepository(service)

    // 2. Trasformiamo i DTO di Firebase nei modelli Tool della UI (Mapping)
    val topTools: StateFlow<List<Tool>> = repository.tools.map { dtoList ->
        dtoList.map { dto ->
            // Trasformiamo ogni ToolDTO in un Tool per la UI
            mapDtoToTool(dto, context)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 3. Facciamo lo stesso per i Lockers
    val lockers: StateFlow<List<Locker>> = repository.lockers.map { dtoList ->
        dtoList.map { dto -> mapDtoToLocker(dto) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Funzioni di utilità per trovare dati
    fun findToolById(id: String): Tool? = topTools.value.find { it.id == id }
    fun findLockerById(id: String): Locker? = lockers.value.find { it.id == id }

    // --- LOGICA DI MAPPING ---
    private fun mapDtoToTool(dto: ToolDTO, context: Context): Tool {
        // Recuperiamo l'ID numerico dell'immagine partendo dal nome stringa nel DB
        val resId = context.resources.getIdentifier(dto.imageResName, "drawable", context.packageName)

        return Tool(
            id = dto.id,
            name = dto.name,
            shortDescription = dto.description.take(50) + "...",
            fullDescription = dto.description,
            imageRes = if (resId != 0) resId else com.example.usetool.R.drawable.placeholder_tool,
            available = dto.type == "noleggio", // Esempio di logica
            pricePerHour = if (dto.type == "noleggio") dto.price else null,
            purchasePrice = if (dto.type == "acquisto") dto.price else null,
            videoUrl = dto.videoUrl,
            pdfUrl = dto.pdfUrls.firstOrNull()
        )
    }

    private fun mapDtoToLocker(dto: LockerDTO): Locker {
        return Locker(
            id = dto.id,
            name = dto.name,
            address = dto.address,
            lat = dto.lat,
            lon = dto.lon,
            toolIds = dto.toolIds
        )
    }
}