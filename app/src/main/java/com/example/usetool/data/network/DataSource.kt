package com.example.usetool.data.network

import com.example.usetool.data.dto.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

//Prende i riferimenti dal FirebaseDao e li trasforma in Flow

class DataSource(private val dao: FirebaseProvider) {

    //Funzione generica per osservare liste di dati su Firebase
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

    //Funzione generica per osservare un singolo oggetto
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

    // Liste di dati - pubblici
    fun observeTools(): Flow<List<ToolDTO>> = observeList(dao.getToolsRef(), ToolDTO::class.java)
    fun observeLockers(): Flow<List<LockerDTO>> = observeList(dao.getLockersRef(), LockerDTO::class.java)
    fun observeExperts(): Flow<List<ExpertDTO>> = observeList(dao.getExpertsRef(), ExpertDTO::class.java)
    fun observeSlots(): Flow<List<SlotDTO>> = observeList(dao.getSlotsRef(), SlotDTO::class.java)

    // Dato riferito al singolo utente - privato
    fun observeUserCart(userId: String): Flow<CartDTO?> =
        observeSingle(dao.getCartsRef().child(userId), CartDTO::class.java)
    fun observePurchases(userId: String): Flow<List<PurchaseDTO>> =
        observeList(dao.getPurchasesRef().child(userId), PurchaseDTO::class.java)
    fun observeRentals(userId: String): Flow<List<RentalDTO>> =
        observeList(dao.getRentalsRef().child(userId), RentalDTO::class.java)
    fun observeProfile(userId: String): Flow<UserDTO?> =
        observeSingle(dao.getUsersRef().child(userId), UserDTO::class.java)
    // In DataSource.kt
    fun observeArduinoState(): Flow<ArduinoStateDto?> =
        observeSingle(dao.getArduinoRef(), ArduinoStateDto::class.java)

    // Funzione di Lettura
    fun updateMultipleNodes(updates: Map<String, Any?>) {
        dao.getToolsRef().parent?.updateChildren(updates)
    }
}