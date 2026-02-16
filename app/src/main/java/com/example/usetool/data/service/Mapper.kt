package com.example.usetool.data.service

import com.example.usetool.data.dto.*
import com.example.usetool.data.dao.*

// --- TOOL (Anagrafica Strumento) ---
fun ToolDTO.toEntity(): ToolEntity = ToolEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    type = type,
    category = category,
    imageResName = imageResName,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    pdfUrls = pdfUrls
)

fun ToolEntity.toDto(): ToolDTO = ToolDTO(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    type = this.type,
    category = this.category,
    imageResName = this.imageResName,
    imageUrl = this.imageUrl,
    videoUrl = this.videoUrl,
    pdfUrls = this.pdfUrls
)

fun List<ToolDTO>.toToolEntityList(): List<ToolEntity> = this.map { it.toEntity() }

// --- SLOT (Disponibilità Fisica nel Locker) ---
fun SlotDTO.toEntity(): SlotEntity = SlotEntity(
    id = id,
    lockerId = lockerId,
    toolId = toolId,
    status = status,
    isFavorite = isFavorite,
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
    toolIds = toolIds
)

fun List<LockerDTO>.toLockerEntityList(): List<LockerEntity> = this.map { it.toEntity() }

// --- EXPERT ---
fun ExpertDTO.toEntity(): ExpertEntity = ExpertEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    profession = profession,
    bio = bio,
    focus = focus,
    phoneNumber = phoneNumber,
    imageUrl = imageUrl
)

fun List<ExpertDTO>.toExpertEntityList(): List<ExpertEntity> = this.map { it.toEntity() }

// --- USER ---
fun UserDTO.toEntity(): UserEntity = UserEntity(
    uid = id ?: "",
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

// --- PURCHASE (Acquisto) ---
fun PurchaseDTO.toEntity(): PurchaseEntity = PurchaseEntity(
    id = id ?: "",
    userId = userId ?: "",
    cartId = cartId ?: "",
    toolId = toolId ?: "",
    toolName = toolName ?: "",
    prezzoPagato = prezzoPagato,
    dataAcquisto = dataAcquisto,
    lockerId = lockerId ?: "",
    slotId = slotId ?: "",
    dataRitiro = dataRitiro ?: 0L,
    dataRitiroEffettiva = dataRitiroEffettiva,
    idTransazionePagamento = idTransazionePagamento
)

fun PurchaseEntity.toFirebaseMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "cartId" to cartId,
    "toolId" to toolId,
    "toolName" to toolName,
    "prezzoPagato" to prezzoPagato,
    "dataAcquisto" to dataAcquisto,
    "dataRitiro" to dataRitiro,
    "dataRitiroEffettiva" to dataRitiroEffettiva,
    "lockerId" to lockerId,
    "slotId" to slotId,
    "idTransazionePagamento" to idTransazionePagamento
)

fun List<PurchaseDTO>.toPurchaseEntityList(): List<PurchaseEntity> = this.map { it.toEntity() }

// --- RENTAL (Noleggio) ---
/**
 * 🔥 CORRETTO: Aggiunta gestione valori di default per evitare crash in Room
 * durante la lettura da Firebase (noleggi "null" o incompleti).
 */
fun RentalDTO.toEntity(): RentalEntity = RentalEntity(
    id = id ?: "",
    userId = userId ?: "",
    toolId = toolId ?: "",
    toolName = toolName ?: "Strumento",
    lockerId = lockerId ?: "",
    slotId = slotId ?: "",
    dataInizio = dataInizio,
    dataFinePrevista = dataFinePrevista,
    dataRiconsegnaEffettiva = dataRiconsegnaEffettiva,
    statoNoleggio = statoNoleggio ?: "ATTIVO",
    costoTotale = costoTotale
)

fun RentalEntity.toFirebaseMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "toolId" to toolId,
    "toolName" to toolName,
    "lockerId" to lockerId,
    "slotId" to slotId,
    "dataInizio" to dataInizio,
    "dataFinePrevista" to dataFinePrevista,
    "dataRiconsegnaEffettiva" to dataRiconsegnaEffettiva,
    "statoNoleggio" to statoNoleggio,
    "costoTotale" to costoTotale
)

fun List<RentalDTO>.toRentalEntityList(): List<RentalEntity> = this.map { it.toEntity() }

// --- CART ---
fun CartDTO.toEntity(): CartEntity = CartEntity(
    id = id ?: "",
    userId = userId ?: "",
    status = status ?: "PENDING",
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