package com.example.usetool.data

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DatabaseInitializer {

    fun runFullSetup(context: Context) {
        val db = FirebaseDatabase.getInstance().reference
        val gson = Gson()

        val filesToLoad = mapOf(
            "tools.json" to "tools",
            "lockers.json" to "lockers",
            "slots.json" to "slots",
            "experts.json" to "experts"
        )

        filesToLoad.forEach { (fileName, nodeName) ->
            // CONTROLLO DI SICUREZZA: Controlliamo se il nodo esiste già
            db.child(nodeName).get().addOnSuccessListener { snapshot ->
                if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                    Log.d("DB_INIT", "Nodo $nodeName vuoto. Caricamento dati da $fileName...")
                    loadNode(context, fileName, nodeName, db, gson)
                } else {
                    Log.d("DB_INIT", "Nodo $nodeName già popolato. Salto il caricamento.")
                }
            }
        }
    }

    private fun loadNode(context: Context, fileName: String, nodeName: String, db: com.google.firebase.database.DatabaseReference, gson: Gson) {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = gson.fromJson(jsonString, mapType)

            db.child(nodeName).setValue(data)
                .addOnSuccessListener {
                    Log.d("DB_INIT", "Successo: $fileName caricato in $nodeName")
                }
                .addOnFailureListener { e ->
                    Log.e("DB_INIT", "Errore in $nodeName: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("DB_INIT", "Errore lettura $fileName: ${e.message}")
        }
    }
}