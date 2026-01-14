package com.example.usetool.data.network

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDao {
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getToolsRef() = db.child("tools")
    fun getLockersRef() = db.child("lockers")
    fun getExpertsRef() = db.child("experts")
    fun getSlotsRef() = db.child("slots")
}