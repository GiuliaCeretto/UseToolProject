package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "rentals")
data class RentalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val toolId: String,
    val toolName: String,
    val lockerId: String,
    val slotId: String,
    val dataInizio: Long,
    val dataFinePrevista: Long?,
    val dataRiconsegnaEffettiva: Long?,
    val statoNoleggio: String,
    val costoTotale: Double
)

@Dao
interface RentalDao {
    @Query("SELECT * FROM rentals ORDER BY dataInizio DESC")
    fun getAllRentals(): Flow<List<RentalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentals(rentals: List<RentalEntity>)

    @Query("DELETE FROM rentals")
    suspend fun clearAll()
}