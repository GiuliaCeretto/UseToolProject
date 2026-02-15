package com.example.usetool.data.service

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*

// --- TOOL ---
fun ToolDTO.toEntity(): ToolEntity = ToolEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    type = type,
    category = category,
    imageResName = imageResName,
    imageUrl = imageUrl, // AGGIUNTO: Allineato con ToolDTO e ToolDao
    videoUrl = videoUrl,
    pdfUrls = pdfUrls,   // AGGIUNTO: Gestito dal Converter in Room
    quantity = quantity
)

fun ToolEntity.toDto(): ToolDTO = ToolDTO(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type,
    category = this.category,
    imageResName = this.imageResName,
    imageUrl = this.imageUrl, // AGGIUNTO
    videoUrl = this.videoUrl,
    pdfUrls = this.pdfUrls,   // AGGIUNTO
    quantity = this.quantity
)

fun List<ToolDTO>.toToolEntityList(): List<ToolEntity> = this.map { it.toEntity() }

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

fun List<SlotDTO>.toSlotEntityList(): List<SlotEntity> = this.map { it.toEntity() }

// --- LOCKER ---
fun LockerDTO.toEntity(): LockerEntity = LockerEntity(
    id = id,
    name = name,
    address = address,
    city = city,
    zipCode = zipCode,
    lat = lat,
    lon = lon,
    saleSlotsCount = saleSlotsCount,
    rentalSlotsCount = rentalSlotsCount,
    macAddress = macAddress,
    toolIds = toolIds // AGGIUNTO: Gestito dal Converter in Room
)

fun List<LockerDTO>.toLockerEntityList(): List<LockerEntity> = this.map { it.toEntity() }

// --- EXPERT ---
fun ExpertDTO.toEntity(): ExpertEntity = ExpertEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    profession = profession,
    bio = bio,
    phoneNumber = phoneNumber,
    imageUrl = imageUrl
)

fun List<ExpertDTO>.toExpertEntityList(): List<ExpertEntity> = this.map { it.toEntity() }

// --- USER ---
fun UserDTO.toEntity(): UserEntity = UserEntity(
    uid = id ?: "", // Usa l'id del DTO (che è l'UID di Firebase)
    email = email ?: "",
    nome = nome ?: "",
    cognome = cognome ?: "",
    telefono = telefono ?: "",
    indirizzo = indirizzo ?: ""
)

fun UserEntity.toDto(): UserDTO = UserDTO(
    id = this.uid,
    email = this.email,
    nome = this.nome,
    cognome = this.cognome,
    telefono = this.telefono,
    indirizzo = this.indirizzo
)

// --- PURCHASE ---
fun PurchaseDTO.toEntity(): PurchaseEntity = PurchaseEntity(
    id = id ?: "",
    userId = userId ?: "",
    cartId = cartId ?: "", // AGGIUNTO: Allineato con PurchaseDTO aggiornato
    toolId = toolId ?: "",
    toolName = toolName ?: "",
    prezzoPagato = prezzoPagato,
    dataAcquisto = dataAcquisto,
    lockerId = lockerId ?: "",
    slotId = slotId ?: "",
    dataRitiro = dataRitiro ?: 0L,
    dataRitiroEffettiva = dataRitiroEffettiva,
    idTransazionePagamento = idTransazionePagamento // AGGIUNTO: Allineato con PurchaseEntity
)

fun List<PurchaseDTO>.toPurchaseEntityList(): List<PurchaseEntity> = this.map { it.toEntity() }

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
    dataRiconsegnaEffettiva = dataRiconsegnaEffettiva,
    statoNoleggio = statoNoleggio,
    costoTotale = costoTotale
)

fun List<RentalDTO>.toRentalEntityList(): List<RentalEntity> = this.map { it.toEntity() }

// --- CART ---
fun CartDTO.toEntity(): CartEntity = CartEntity(
    id = id ?: "",
    userId = userId ?: "",
    status = status,
    totaleProvvisorio = totaleProvvisorio,
    ultimoAggiornamento = ultimoAggiornamento
)

fun CartEntity.toDto(itemsMap: Map<String, SlotDTO>): CartDTO = CartDTO(
    id = this.id,
    userId = this.userId,
    items = itemsMap,
    totaleProvvisorio = this.totaleProvvisorio,
    status = this.status,
    ultimoAggiornamento = this.ultimoAggiornamento
)