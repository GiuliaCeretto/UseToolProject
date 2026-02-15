package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tools")
data class ToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val type: String,
    val category: String,
    val imageResName: String,
    val imageUrl: String?, // Aggiunto per Firebase Storage
    val videoUrl: String,
    val pdfUrls: List<String>, // Richiede il Converter
    val quantity: Int
)

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<ToolEntity>)

    @Query("DELETE FROM tools")
    suspend fun clearAll()
}