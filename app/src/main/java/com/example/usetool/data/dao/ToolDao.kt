package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<ToolEntity>)

    @Query("DELETE FROM tools")
    suspend fun clearAll()
}

@Entity(tableName = "tools") // Specifica il nome della tabella [cite: 144]
data class ToolEntity(
    @PrimaryKey val id: String, // Ogni entità deve definire una Primary Key [cite: 177]
    val name: String,
    val description: String,
    val price: Double,
    val type: String
)