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

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cartId")]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val cartId: String,
    val slotId: String,
    val toolId: String,
    val lockerId: String,
    val toolName: String,
    val price: Double,
    val quantity: Int
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    fun getActiveCart(userId: String): Flow<CartEntity?>

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    fun getItemsByCartId(cartId: String): Flow<List<CartItemEntity>>

    // 🔥 INDISPENSABILE: Per lo scudo nel repository degli inventari
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items")
    suspend fun getAllCartItemsSnapshot(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun deleteItemsByCartId(cartId: String)

    @Query("DELETE FROM cart WHERE id = :cartId")
    suspend fun deleteCart(cartId: String)

    @Transaction
    suspend fun updateFullCart(cart: CartEntity, items: List<CartItemEntity>) {
        insertCart(cart)
        deleteItemsByCartId(cart.id)
        insertCartItems(items)
    }

    @Transaction
    suspend fun clearCartLocally(cartId: String) {
        deleteItemsByCartId(cartId)
        deleteCart(cartId)
    }
}