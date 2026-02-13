package com.example.usetool.data.service

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*

// --- TOOL ---
fun ToolDTO.toEntity(): ToolEntity = ToolEntity(id, name, description, price, type)
fun List<ToolDTO>.toEntityList(): List<ToolEntity> = this.map { it.toEntity() }

// --- SLOT ---
fun SlotDTO.toEntity(): SlotEntity = SlotEntity(id, lockerId, toolId, status, quantity)
fun List<SlotDTO>.toEntityList(): List<SlotEntity> = this.map { it.toEntity() }

// --- LOCKER ---
fun LockerDTO.toEntity(): LockerEntity = LockerEntity(
    id = this.id,
    name = this.name,
    address = this.address,
    city = this.city,
    lat = this.lat ,
    lon = this.lon
)
fun List<LockerDTO>.toEntityList(): List<LockerEntity> = this.map { it.toEntity() }

// --- PURCHASE ---
fun PurchaseDTO.toEntity(): PurchaseEntity = PurchaseEntity(
    id = this.id ?: "",
    toolName = this.toolName ?: "",
    prezzoPagato = this.prezzoPagato,
    dataAcquisto = this.dataAcquisto,
    lockerId = this.lockerId ?: ""
)
fun List<PurchaseDTO>.toEntityList(): List<PurchaseEntity> = this.map { it.toEntity() }

// --- RENTAL ---
fun RentalDTO.toEntity(): RentalEntity = RentalEntity(
    id = this.id ?: "",
    userId = this.userId ?: "",
    toolId = this.toolId ?: "",
    toolName = this.toolName ?: "",
    lockerId = this.lockerId ?: "",
    slotId = this.slotId ?: "",
    dataInizio = this.dataInizio,
    dataFinePrevista = this.dataFinePrevista,
    statoNoleggio = this.statoNoleggio,
    costoTotale = this.costoTotale
)
fun List<RentalDTO>.toEntityList(): List<RentalEntity> = this.map { it.toEntity() }

fun ExpertDTO.toEntity(): ExpertEntity = ExpertEntity(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    profession = this.profession,
    bio = this.bio
)
fun List<ExpertDTO>.toEntityList(): List<ExpertEntity> = this.map { it.toEntity() }

// --- CART ---
fun CartDTO.toEntity(): CartEntity = CartEntity(
    id = this.id ?: "",
    userId = this.userId ?: "",
    status = this.status, // Già non null
    totaleProvvisorio = this.totaleProvvisorio, // Già non null
    ultimoAggiornamento = this.ultimoAggiornamento // Già non null
)

// --- USER ---
fun UserDTO.toEntity(): UserEntity = UserEntity(
    email = this.email ?: "",
    nome = this.nome ?: "",
    cognome = this.cognome ?: "",
    telefono = this.telefono ?: "",
    indirizzo = this.indirizzo ?: ""
)