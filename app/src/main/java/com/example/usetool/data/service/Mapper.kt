package com.example.usetool.data.service

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*

// --- TOOL ---
fun ToolDTO.toEntity(): ToolEntity = ToolEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    type = type
)

fun ToolEntity.toDto(): ToolDTO = ToolDTO(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    quantity = 1, // Parametro obbligatorio in ToolDTO senza default
    type = this.type
)

fun List<ToolDTO>.toEntityList(): List<ToolEntity> = this.map { it.toEntity() }

// --- SLOT ---
fun SlotDTO.toEntity(): SlotEntity = SlotEntity(
    id = id,
    lockerId = lockerId,
    toolId = toolId,
    status = status,
    quantity = quantity
)

fun SlotEntity.toDto(): SlotDTO = SlotDTO(
    id = this.id,
    lockerId = this.lockerId,
    toolId = this.toolId,
    status = this.status,
    quantity = this.quantity
)

fun List<SlotDTO>.toEntityList(): List<SlotEntity> = this.map { it.toEntity() }

// --- USER ---
fun UserDTO.toEntity(): UserEntity = UserEntity(
    email = email ?: "",
    nome = nome ?: "",
    cognome = cognome ?: "",
    telefono = telefono ?: "",
    indirizzo = indirizzo ?: ""
)

fun UserEntity.toDto(): UserDTO = UserDTO(
    email = this.email,
    nome = this.nome,
    cognome = this.cognome,
    telefono = this.telefono,
    indirizzo = this.indirizzo
    // passwordHash e passwordSalt rimangono null o gestiti altrove
)

// --- LOCKER ---
fun LockerDTO.toEntity(): LockerEntity = LockerEntity(
    id = id,
    name = name,
    address = address,
    city = city,
    lat = lat,
    lon = lon
)

fun List<LockerDTO>.toEntityList(): List<LockerEntity> = this.map { it.toEntity() }

// --- PURCHASE ---
fun PurchaseDTO.toEntity(): PurchaseEntity = PurchaseEntity(
    id = id ?: "",
    toolName = toolName ?: "",
    prezzoPagato = prezzoPagato,
    dataAcquisto = dataAcquisto,
    lockerId = lockerId ?: ""
)

fun List<PurchaseDTO>.toEntityList(): List<PurchaseEntity> = this.map { it.toEntity() }

// --- RENTAL ---
fun RentalDTO.toEntity(): RentalEntity = RentalEntity(
    id = id ?: "",
    userId = userId ?: "",
    toolId = toolId ?: "",
    toolName = toolName ?: "",
    lockerId = lockerId ?: "",
    slotId = slotId ?: "",
    dataInizio = dataInizio,
    dataFinePrevista = dataFinePrevista,
    statoNoleggio = statoNoleggio ?: "",
    costoTotale = costoTotale
)

fun List<RentalDTO>.toEntityList(): List<RentalEntity> = this.map { it.toEntity() }

// --- EXPERT ---
fun ExpertDTO.toEntity(): ExpertEntity = ExpertEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    profession = profession,
    bio = bio
)

fun List<ExpertDTO>.toEntityList(): List<ExpertEntity> = this.map { it.toEntity() }

// --- CART ---
fun CartDTO.toEntity(): CartEntity = CartEntity(
    id = id ?: "",
    userId = userId ?: "",
    status = status,
    totaleProvvisorio = totaleProvvisorio,
    ultimoAggiornamento = ultimoAggiornamento
)

/**
 * Converte la testata del carrello (Entity) in DTO per Firebase,
 * includendo la mappa degli item convertiti in SlotDTO.
 */
fun CartEntity.toDto(itemsMap: Map<String, SlotDTO>): CartDTO = CartDTO(
    id = this.id,
    userId = this.userId,
    items = itemsMap,
    totaleProvvisorio = this.totaleProvvisorio,
    status = this.status,
    ultimoAggiornamento = this.ultimoAggiornamento
)