package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "slots")
data class SlotEntity(
    @PrimaryKey val id: String,
    val lockerId: String,
    val toolId: String,
    val status: String, // "VUOTO", "DISPONIBILE", "NOLEGGIATO", ecc.
    val quantity: Int
)

@Dao
interface SlotDao {
    @Query("SELECT * FROM slots WHERE lockerId = :lockerId")
    fun getSlotsByLocker(lockerId: String): Flow<List<SlotEntity>>

    @Query("SELECT * FROM slots")
    fun getAllSlots(): Flow<List<SlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<SlotEntity>)

    @Query("DELETE FROM slots")
    suspend fun clearAll()
}