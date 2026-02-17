package com.example.usetool.data.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class LinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "locker_id")
    val lockerId: Int,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "connection_time")
    val connectionTime: String
)

@Dao
interface LinkDao {

    // Cambiato da connectionTime a connection_time
    @Query("SELECT * FROM links ORDER BY connection_time DESC")
    fun getAllLinks(): Flow<List<LinkEntity>>

    @Query("SELECT * FROM links WHERE id = :linkId")
    suspend fun getLinkById(linkId: Int): LinkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LinkEntity)

    @Query("DELETE FROM links")
    suspend fun deleteAll()
}