package com.example.usetool.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val status: String, // "PENDING", "CONFERMATO"
    val totaleProvvisorio: Double,
    val ultimoAggiornamento: Long
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val cartId: String,
    val slotId: String,
    val toolName: String,
    val price: Double
)

@Dao
interface CartDao {

    // --- Operazioni sulla testata del Carrello ---

    @Query("SELECT * FROM cart WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    fun getActiveCart(userId: String): Flow<CartEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Query("DELETE FROM cart WHERE id = :cartId")
    suspend fun deleteCart(cartId: String)

    // --- Operazioni sugli elementi (Items) ---

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    fun getItemsByCartId(cartId: String): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun deleteItemsByCartId(cartId: String)
}