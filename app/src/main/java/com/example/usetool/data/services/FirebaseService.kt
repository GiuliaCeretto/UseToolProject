package com.example.usetool.data.services

import com.example.usetool.data.dto.ExpertDTO
import com.example.usetool.data.dto.LockerDTO
import com.example.usetool.data.dto.ToolDTO
import com.example.usetool.data.network.FirebaseDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseService(private val dao: FirebaseDao) {

    fun observeTools(): Flow<List<ToolDTO>> = callbackFlow {
        val listener = dao.getToolsRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tools = snapshot.children.mapNotNull { it.getValue(ToolDTO::class.java) }
                trySend(tools)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { dao.getToolsRef().removeEventListener(listener) }
    }

    fun observeLockers(): Flow<List<LockerDTO>> = callbackFlow {
        val listener = dao.getLockersRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lockers = snapshot.children.mapNotNull { it.getValue(LockerDTO::class.java) }
                trySend(lockers)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { dao.getLockersRef().removeEventListener(listener) }
    }

    fun observeExperts(): Flow<List<ExpertDTO>> = callbackFlow {
        val listener = dao.getExpertsRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val experts = snapshot.children.mapNotNull { it.getValue(ExpertDTO::class.java) }
                trySend(experts)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { dao.getExpertsRef().removeEventListener(listener) }
    }
}