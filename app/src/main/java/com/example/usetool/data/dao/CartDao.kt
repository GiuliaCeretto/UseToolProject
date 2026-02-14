package com.example.usetool.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val status: String = "PENDING",
    val totaleProvvisorio: Double = 0.0,
    val ultimoAggiornamento: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val cartId: String,
    val slotId: String,
    val toolId: String, // Aggiunto per coerenza con SlotDTO
    val toolName: String,
    val price: Double,
    val quantity: Int = 1
)

@Dao
interface CartDao {

    @Query("SELECT * FROM cart WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    fun getActiveCart(userId: String): Flow<CartEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Query("DELETE FROM cart WHERE id = :cartId")
    suspend fun deleteCart(cartId: String)

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    fun getItemsByCartId(cartId: String): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun deleteItemsByCartId(cartId: String)

    @Transaction
    suspend fun updateFullCart(cart: CartEntity, items: List<CartItemEntity>) {
        insertCart(cart)
        deleteItemsByCartId(cart.id) // Evita duplicati da entryId autoincrementale
        insertCartItems(items)
    }

    @Transaction
    suspend fun clearCartLocally(cartId: String) {
        deleteItemsByCartId(cartId)
        deleteCart(cartId)
    }
}