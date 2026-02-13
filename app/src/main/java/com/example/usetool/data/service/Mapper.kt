package com.example.usetool.data.service

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*


fun ToolDTO.toEntity(): ToolEntity = ToolEntity(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type
)

fun LockerDTO.toEntity(): LockerEntity = LockerEntity(
    id = this.id,
    name = this.name,
    address = this.address,
    city = this.city,
    lat = this.lat,
    lon = this.lon
)

fun ExpertDTO.toEntity(): ExpertEntity = ExpertEntity(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    profession = this.profession,
    bio = this.bio
)

fun UserDTO.toEntity(userEmail: String): UserEntity = UserEntity(
    email = userEmail, // L'email viene usata come PrimaryKey [cite: 177]
    nome = this.nome ?: "",
    cognome = this.cognome ?: "",
    telefono = this.telefono ?: "",
    indirizzo = this.indirizzo ?: ""
)

fun PurchaseDTO.toEntity(): PurchaseEntity = PurchaseEntity(
    id = this.id ?: "",
    toolName = this.toolName ?: "",
    prezzoPagato = this.prezzoPagato,
    dataAcquisto = this.dataAcquisto,
    lockerId = this.lockerId ?: ""
)

fun RentalDTO.toEntity(): RentalEntity = RentalEntity(
    id = this.id ?: "",
    userId = this.userId ?: "",
    toolId = this.toolId ?: "",
    lockerId = this.lockerId ?: "",
    slotId = this.slotId ?: "",
    toolName = this.toolName ?: "",
    statoNoleggio = this.statoNoleggio,
    costoTotale = this.costoTotale,
    dataInizio = this.dataInizio,
    dataFinePrevista = this.dataFinePrevista
)

fun SlotDTO.toEntity(): SlotEntity = SlotEntity(
    id = this.id,
    lockerId = this.lockerId,
    toolId = this.toolId,
    status = this.status,
    quantity = this.quantity
)