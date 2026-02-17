package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "lockers")
data class LockerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val city: String,
    val zipCode: String,
    val lat: Double,
    val lon: Double,
    val saleSlotsCount: Int,
    val rentalSlotsCount: Int,
    val macAddress: String,
    val toolIds: List<String>,
    val linkId: Int = 0
)

@Dao
interface LockerDao {
    @Query("SELECT * FROM lockers")
    fun getAll(): Flow<List<LockerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lockers: List<LockerEntity>)

    @Query("DELETE FROM lockers")
    suspend fun clearAll()
}