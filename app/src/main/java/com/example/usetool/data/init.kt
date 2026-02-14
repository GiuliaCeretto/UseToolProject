package com.example.usetool.data // Spostalo nel nuovo pacchetto se hai seguito la gerarchia

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DatabaseInitializer {

    fun runFullSetup(context: Context) {
        // Sintassi moderna: otteniamo l'istanza senza usare .ktx
        val db = FirebaseDatabase.getInstance().reference
        val gson = Gson()

        // Mappa dei file negli assets e dei relativi nodi su Firebase
        val filesToLoad = mapOf(
            "tools.json" to "tools",
            "lockers.json" to "lockers",
            "slots.json" to "slots",
            "experts.json" to "experts"
        )

        filesToLoad.forEach { (fileName, nodeName) ->
            try {
                // 1. Legge il file dalla cartella assets
                val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

                // 2. Converte il JSON in una mappa compatibile con Firebase
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                val data: Map<String, Any> = gson.fromJson(jsonString, mapType)

                // 3. Carica i dati su Firebase
                db.child(nodeName).setValue(data)
                    .addOnSuccessListener {
                        Log.d("DB_INIT", "Successo: $fileName caricato nel nodo $nodeName")
                    }
                    .addOnFailureListener { e ->
                        Log.e("DB_INIT", "Errore nel caricamento di $fileName: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e("DB_INIT", "File $fileName non trovato o errore lettura: ${e.message}")
            }
        }
    }
}