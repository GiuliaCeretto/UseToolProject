package com.example.usetool.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.usetool.data.dao.*

@Database(
    entities = [
        UserEntity::class,
        LockerEntity::class,
        SlotEntity::class,
        ToolEntity::class,
        RentalEntity::class,
        PurchaseEntity::class,
        ExpertEntity::class,
        CartEntity::class,
        CartItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun lockerDao(): LockerDao
    abstract fun slotDao(): SlotDao
    abstract fun toolDao(): ToolDao
    abstract fun rentalDao(): RentalDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun expertDao(): ExpertDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Versione ottimizzata della funzione getDatabase
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "use_tool_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}