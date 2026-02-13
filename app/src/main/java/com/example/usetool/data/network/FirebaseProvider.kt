package com.example.usetool.data.network

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// Contiene solo i riferimenti ai nodi di Firebase che ci servono per ottenere i dati
class FirebaseProvider {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getToolsRef() = db.child("tools")
    fun getLockersRef() = db.child("lockers")
    fun getExpertsRef() = db.child("experts")
    fun getSlotsRef() = db.child("slots")

    fun getPurchasesRef(): DatabaseReference = db.child("purchases")
    fun getRentalsRef(): DatabaseReference = db.child("rentals")
    fun getCartsRef(): DatabaseReference = db.child("carts")
    fun getUsersRef(): DatabaseReference = db.child("users")
}