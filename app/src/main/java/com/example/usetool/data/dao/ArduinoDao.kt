package com.example.usetool.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "arduino_status")
data class ArduinoEntity(
    @PrimaryKey val id: String = "current_status", // Usiamo un ID fisso per avere una sola riga di stato
    val isConnected: Boolean,
    val openBuyDoor: Boolean,
    val openRentDoor: Boolean,
    val lastSync: Long = System.currentTimeMillis()
)

@Dao
interface ArduinoDao {
    @Query("SELECT * FROM arduino_status WHERE id = 'current_status'")
    fun getArduinoStatus(): Flow<ArduinoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: ArduinoEntity)

    @Query("DELETE FROM arduino_status")
    suspend fun clearStatus()
}