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
    val status: String, // "VUOTO", "DISPONIBILE", "PRENOTATO", ecc.
    val quantity: Int,
    val isFavorite: Boolean // <-- AGGIUNTO: logica preferito legata allo slot fisico
)

@Dao
interface SlotDao {
    @Query("SELECT * FROM slots WHERE lockerId = :lockerId")
    fun getSlotsByLocker(lockerId: String): Flow<List<SlotEntity>>

    @Query("SELECT * FROM slots")
    fun getAllSlots(): Flow<List<SlotEntity>>

    // Query per recuperare solo gli slot che l'utente ha segnato come preferiti
    @Query("SELECT * FROM slots WHERE isFavorite = 1")
    fun getFavoriteSlots(): Flow<List<SlotEntity>>

    // Metodo per attivare/disattivare il preferito sullo specifico slot
    @Query("UPDATE slots SET isFavorite = :isFavorite WHERE id = :slotId")
    suspend fun toggleFavorite(slotId: String, isFavorite: Boolean)

    @Query("UPDATE slots SET status = :newStatus WHERE id = :slotId")
    suspend fun updateSlotStatus(slotId: String, newStatus: String)

    @Query("UPDATE slots SET status = :newStatus, quantity = :newQuantity WHERE id = :slotId")
    suspend fun updateSlotStock(slotId: String, newStatus: String, newQuantity: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<SlotEntity>)

    @Query("DELETE FROM slots")
    suspend fun clearAll()
}