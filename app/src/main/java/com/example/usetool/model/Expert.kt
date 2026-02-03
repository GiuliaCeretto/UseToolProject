package com.example.usetool.model

data class Expert(
    val id: String,
    val name: String,
    val imageRes: Int? = null,
    val profession: String = "",
    val description: String = ""
)
