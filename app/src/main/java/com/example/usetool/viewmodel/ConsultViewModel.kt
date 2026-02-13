package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usetool.data.dto.ExpertDTO
import com.example.usetool.data.repository.ExpertRepository
import kotlinx.coroutines.flow.*

class ConsultViewModel(
    private val expertRepo: ExpertRepository
) : ViewModel() {

    // Query di ricerca inserita dall'utente
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Stato per il caricamento (opzionale ma consigliato)
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Trasformiamo il Flow degli esperti del Repository in una lista filtrata.
     * Usiamo 'combine' per reagire sia ai cambi nel DB che ai cambi nella ricerca.
     */
    val experts: StateFlow<List<ExpertDTO>> = expertRepo.experts
        .onEach { _isLoading.value = false } // Nascondi loader quando arrivano i dati
        .combine(_searchQuery) { expertsList, query ->
            if (query.isBlank()) {
                expertsList // Mostra tutto se non c'è ricerca
            } else {
                expertsList.filter { expert ->
                    // Filtra per nome, cognome o professione (case-insensitive)
                    expert.firstName.contains(query, ignoreCase = true) ||
                            expert.lastName.contains(query, ignoreCase = true) ||
                            expert.profession.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Funzione per aggiornare la query dalla UI (es. da una TextField)
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }


    fun findExpertById(id: String): ExpertDTO? {
        return experts.value.find { it.id == id }
    }
}