package com.example.usetool.data.network

import com.example.usetool.data.dto.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DataSource(private val dao: FirebaseProvider) {

    // --- FUNZIONI GENERICHE PER FLOW ---

    private fun <T> observeList(ref: DatabaseReference, clazz: Class<T>): Flow<List<T>> = callbackFlow {
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    runCatching { it.getValue(clazz) }.getOrNull()
                }
                trySend(items)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun <T> observeSingle(ref: DatabaseReference, clazz: Class<T>): Flow<T?> = callbackFlow {
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(clazz))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- 1. LISTE PUBBLICHE (Catalogo e Community) ---

    fun observeTools(): Flow<List<ToolDTO>> = observeList(dao.getToolsRef(), ToolDTO::class.java)
    fun observeLockers(): Flow<List<LockerDTO>> = observeList(dao.getLockersRef(), LockerDTO::class.java)
    fun observeSlots(): Flow<List<SlotDTO>> = observeList(dao.getSlotsRef(), SlotDTO::class.java)
    fun observeExperts(): Flow<List<ExpertDTO>> = observeList(dao.getExpertsRef(), ExpertDTO::class.java)

    // --- 2. DATI PRIVATI UTENTE (Profilo e Carrello) ---

    fun observeProfile(userId: String): Flow<UserDTO?> =
        observeSingle(dao.getUsersRef().child(userId), UserDTO::class.java)

    fun observeUserCart(userId: String): Flow<CartDTO?> =
        observeSingle(dao.getCartsRef().child(userId), CartDTO::class.java)

    // --- 3. STORICO ORDINI (Purchases & Rentals) ---

    fun observePurchases(userId: String): Flow<List<PurchaseDTO>> =
        observeList(dao.getPurchasesRef().child(userId), PurchaseDTO::class.java)

    fun observeRentals(userId: String): Flow<List<RentalDTO>> =
        observeList(dao.getRentalsRef().child(userId), RentalDTO::class.java)

    // --- 4. HARDWARE (Arduino) ---

    fun observeArduino(): Flow<ArduinoStateDto?> =
        observeSingle(dao.getArduinoRef(), ArduinoStateDto::class.java)

    // --- 5. FUNZIONI DI SCRITTURA (Network Output) ---

    // Scrittura singola (es: comando sblocco porta)
    suspend fun updateNode(path: String, value: Any?) {
        dao.getToolsRef().parent?.child(path)?.setValue(value)?.await()
    }

    // Scrittura multipla atomica (es: aggiorna carrello e stato slot insieme)
    fun updateMultipleNodes(updates: Map<String, Any?>) {
        dao.getToolsRef().parent?.updateChildren(updates)
    }

    // Nel DataSource.kt
    suspend fun updateArduinoNode(path: String, value: Any?) {
        // Punta direttamente a arduino -> openBuyDoor (o altro) e sovrascrive il valore
        dao.getArduinoRef().child(path).setValue(value).await()
    }

    fun updateArduinoMultiple(updates: Map<String, Any?>) {
        // updateChildren su "arduino" modifica solo le chiavi passate nella mappa
        dao.getArduinoRef().updateChildren(updates)
    }
}