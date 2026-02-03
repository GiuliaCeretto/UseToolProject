package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.Expert
import com.example.usetool.R

class ConsultViewModel : ViewModel() {

    // Lista di esperti
    private val _experts = MutableStateFlow<List<Expert>>(
        listOf(
            Expert(
                id = "e1",
                name = "Mario Rossi",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Idraulico",
                description = "Specializzato in impianti domestici e riparazioni rapide."
            ),
            Expert(
                id = "e2",
                name = "Luca Bianchi",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Elettricista",
                description = "Esperto in impianti elettrici residenziali e industriali."
            ),
            Expert(
                id = "e3",
                name = "Giulia Verdi",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Falegname",
                description = "Specializzata in mobili su misura e restauro legno."
            ),
            Expert(
                id = "e4",
                name = "Francesco Neri",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Carpentiere",
                description = "Esperto in strutture in legno e carpenteria generale."
            ),
            Expert(
                id = "e5",
                name = "Sara Galli",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Pittore",
                description = "Professionista in decorazioni e finiture di interni."
            ),
            Expert(
                id = "e6",
                name = "Alessandro Conti",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Muratore",
                description = "Specializzato in lavori di muratura e ristrutturazioni."
            ),
            Expert(
                id = "e7",
                name = "Chiara Romano",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Giardiniere",
                description = "Esperta in cura del verde e progettazione giardini."
            ),
            Expert(
                id = "e8",
                name = "Matteo De Luca",
                imageRes = R.drawable.placeholder_profilo,
                profession = "Tecnico informatico",
                description = "Specializzato in assistenza hardware e software."
            )
        )
    )

    val experts: StateFlow<List<Expert>> = _experts

    // Funzione per trovare un esperto tramite ID
    fun findExpertById(id: String): Expert? = _experts.value.find { it.id == id }
}
