package com.example.usetool.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.usetool.model.Tool
import com.example.usetool.model.Locker
import com.example.usetool.R

class UseToolViewModel : ViewModel() {

    private val _topTools = MutableStateFlow<List<Tool>>(emptyList())
    val topTools: StateFlow<List<Tool>> = _topTools

    private val _lockers = MutableStateFlow<List<Locker>>(emptyList())
    val lockers: StateFlow<List<Locker>> = _lockers

    init {
        loadMocks()
    }

    private fun loadMocks() {
        val tools = listOf(

            Tool(
                id = "1",
                name = "Trapano a percussione",
                shortDescription = "Trapano 18V potente e versatile",
                fullDescription = "Trapano a percussione ideale per forare muro, legno e metallo. Dotato di batteria a lunga durata, velocità regolabile e impugnatura ergonomica.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                pricePerHour = 1.50,
                technicalData = mapOf(
                    "Voltaggio" to "18V",
                    "Autonomia" to "3h",
                    "Peso" to "2.4 kg"
                ),
                pdfUrl = "https://example.com/trapano.pdf",
                videoUrl = "https://youtube.com/watch?v=trapano"
            ),

            Tool(
                id = "2",
                name = "Pinza a pappagallo",
                shortDescription = "Pinza regolabile per serraggi vari",
                fullDescription = "Pinza a pappagallo regolabile, utile per serraggi e fissaggi su tubi e profilati.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                pricePerHour = 0.80, // affittabile
                technicalData = mapOf(
                    "Apertura" to "25 mm",
                    "Lunghezza" to "200 mm",
                    "Peso" to "0.5 kg"
                ),
                pdfUrl = "https://example.com/pinza.pdf",
                videoUrl = "https://youtube.com/watch?v=pinza"
            ),

            Tool(
                id = "3",
                name = "Kit tasselli",
                shortDescription = "Kit completo di tasselli per fissaggi",
                fullDescription = "Kit di tasselli di varie misure, ideale per fissaggi su muro e legno. Acquistabile in confezione completa.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                purchasePrice = 9.90,
                technicalData = mapOf(
                    "Quantità" to "50 pezzi",
                    "Misura" to "6 mm",
                    "Tipologia" to "Acciaio"
                ),
                pdfUrl = "https://example.com/tasselli.pdf",
                videoUrl = "https://youtube.com/watch?v=tasselli"
            ),

            Tool(
                id = "4",
                name = "Martello",
                shortDescription = "Martello universale in acciaio",
                fullDescription = "Martello in acciaio temperato con manico antiscivolo. Perfetto per lavori di carpenteria e bricolage.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                purchasePrice = 14.90,
                technicalData = mapOf(
                    "Peso" to "0.9 kg",
                    "Lunghezza" to "30 cm"
                ),
                pdfUrl = "https://example.com/martello.pdf",
                videoUrl = "https://youtube.com/watch?v=martello"
            ),

            Tool(
                id = "5",
                name = "Sega circolare",
                shortDescription = "Sega per tagli precisi",
                fullDescription = "Sega circolare ad alte prestazioni per tagli precisi su legno e pannelli. Dotata di protezione lama e guida laterale.",
                imageRes = R.drawable.placeholder_tool,
                available = false,
                pricePerHour = 2.00,
                technicalData = mapOf(
                    "Voltaggio" to "230V",
                    "Peso" to "3.8 kg"
                ),
                pdfUrl = "https://example.com/sega.pdf",
                videoUrl = "https://youtube.com/watch?v=sega"
            ),

            // NUOVI STRUMENTI
            Tool(
                id = "6",
                name = "Chiodi",
                shortDescription = "Confezione di chiodi in acciaio",
                fullDescription = "Chiodi robusti in acciaio, disponibili in varie lunghezze. Perfetti per lavori di falegnameria e bricolage.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                purchasePrice = 4.50,
                technicalData = mapOf(
                    "Quantità" to "100 pezzi",
                    "Misura" to "5 cm",
                    "Materiale" to "Acciaio"
                ),
                pdfUrl = "https://example.com/chiodi.pdf",
                videoUrl = "https://youtube.com/watch?v=chiodi"
            ),

            Tool(
                id = "7",
                name = "Set di cacciaviti",
                shortDescription = "Kit di cacciaviti multipli",
                fullDescription = "Set di cacciaviti di varie misure, ideale per lavori di precisione. Affittabile per uso temporaneo.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                pricePerHour = 1.00,
                technicalData = mapOf(
                    "Pezzi" to "10",
                    "Tipologia" to "Philips / Piatto",
                    "Manico" to "Antiscivolo"
                ),
                pdfUrl = "https://example.com/cacciaviti.pdf",
                videoUrl = "https://youtube.com/watch?v=cacciaviti"
            ),

            Tool(
                id = "8",
                name = "Carta vetrata",
                shortDescription = "Foglio carta vetrata grana media",
                fullDescription = "Carta vetrata resistente per levigatura legno, metallo e plastica. Acquistabile in confezione singola.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                purchasePrice = 2.50,
                technicalData = mapOf(
                    "Grana" to "120",
                    "Dimensione" to "230x280 mm"
                ),
                pdfUrl = "https://example.com/cartavetrata.pdf",
                videoUrl = "https://youtube.com/watch?v=cartavetrata"
            ),

            Tool(
                id = "9",
                name = "Fascette",
                shortDescription = "Fascette in plastica per fissaggi",
                fullDescription = "Fascette resistenti in plastica, ideali per cablaggi e fissaggi vari. Acquistabili in confezione.",
                imageRes = R.drawable.placeholder_tool,
                available = true,
                purchasePrice = 3.00,
                technicalData = mapOf(
                    "Lunghezza" to "200 mm",
                    "Colore" to "Nero",
                    "Quantità" to "50 pezzi"
                ),
                pdfUrl = "https://example.com/fascette.pdf",
                videoUrl = "https://youtube.com/watch?v=fascette"
            )
        )

        _topTools.value = tools.take(9)

        _lockers.value = listOf(
            Locker("d1","Ferramenta Centrale","Via A, 1", 45.0703, 7.6869, listOf("1","2","3","6","7")),
            Locker("d2","Bricolage Store","Via B, 2", 45.0710, 7.6900, listOf("2","4","8")),
            Locker("d3","Mini Store","Via C, 3",45.0680,7.6920, listOf("1","5","9"))
        )
    }


    fun findToolById(id: String): Tool? = (_topTools.value + _topTools.value).find { it.id == id } // naive
    fun findLockerById(id: String): Locker? = _lockers.value.find { it.id == id }
}