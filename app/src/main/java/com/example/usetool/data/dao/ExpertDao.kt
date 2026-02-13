package com.example.usetool.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "experts")
data class ExpertEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val profession: String,
    val bio: String,
    val phoneNumber: String = "",
    val imageUrl: String = ""
)

@Dao
interface ExpertDao {
    @Query("SELECT * FROM experts")
    fun getAll(): Flow<List<ExpertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(experts: List<ExpertEntity>)
}