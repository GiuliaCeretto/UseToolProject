package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val cartId: String, // Aggiunto
    val toolId: String,
    val toolName: String,
    val prezzoPagato: Double,
    val dataAcquisto: Long,
    val dataRitiro: Long,
    val dataRitiroEffettiva: Long?,
    val lockerId: String,
    val slotId: String,
    val idTransazionePagamento: String? // Aggiunto
)

@Dao
interface PurchaseDao {

    @Query("SELECT * FROM purchases ORDER BY dataAcquisto DESC")
    fun getAll(): Flow<List<PurchaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(purchases: List<PurchaseEntity>)

    @Query("DELETE FROM purchases")
    suspend fun clearAll()
}