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
    pdfUrls = pdfUrls,
    autonomia = autonomia,
    potenza = potenza,
    peso = peso
)

fun ToolEntity.toDto(): ToolDTO = ToolDTO(
    id = id,
    name = name,
    description = description,
    price = price,
    type = type,
    category = category,
    imageResName = imageResName,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    pdfUrls = pdfUrls,
    autonomia = autonomia,
    potenza = potenza,
    peso = peso
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
/**
 * Mappatura del Locker: include linkId (Int) per lo sblocco hardware
 * e id (String) per l'identificazione nel database.
 */
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
    toolIds = toolIds,
    linkId = linkId // Fondamentale per il collegamento hardware
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
    lockerId = lockerId ?: "", // Salvato come linkId (String) per il filtraggio
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
fun RentalDTO.toEntity(): RentalEntity = RentalEntity(
    id = id ?: "",
    userId = userId ?: "",
    toolId = toolId ?: "",
    toolName = toolName ?: "Strumento",
    lockerId = lockerId ?: "", // Utilizza il linkId numerico mappato come stringa
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

// --- MAPPER CART ITEM → PURCHASE ---
fun CartItemEntity.toPurchaseEntity(): PurchaseEntity = PurchaseEntity(
    id = slotId, // puoi usare slotId come ID univoco o generare un UUID se preferisci
    userId = "", // eventualmente puoi passare l'userId se lo hai disponibile
    cartId = cartId,
    toolId = toolId,
    toolName = toolName,
    prezzoPagato = price,
    dataAcquisto = System.currentTimeMillis(), // oppure passa la data reale se disponibile
    dataRitiro = 0L, // da impostare quando necessario
    dataRitiroEffettiva = null,
    lockerId = lockerId,
    slotId = slotId,
    idTransazionePagamento = null
)


// --- LINK (Connessioni Locker) ---
/**
 * Mappatura per il log delle connessioni hardware.
 */
fun LinkDTO.toEntity(): LinkEntity = LinkEntity(
    id = id,
    lockerId = lockerId, // Rappresenta il linkId (Int)
    userId = userId,     // Hash numerico dell'UID utente
    connectionTime = connectionTime
)

fun LinkEntity.toDto(): LinkDTO = LinkDTO(
    id = this.id,
    lockerId = this.lockerId,
    userId = this.userId,
    connectionTime = this.connectionTime
)

fun List<LinkDTO>.toLinkEntityList(): List<LinkEntity> = this.map { it.toEntity() }
