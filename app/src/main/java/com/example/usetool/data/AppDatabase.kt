package com.example.usetool.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.usetool.data.dao.*

@Database(
    entities = [
        ToolEntity::class,
        ExpertEntity::class,
        LockerEntity::class,
        PurchaseEntity::class,
        UserEntity::class,
        RentalEntity::class,
        SlotEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun toolDao(): ToolDao
    abstract fun expertDao(): ExpertDao
    abstract fun lockerDao(): LockerDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun userDao(): UserDao
    abstract fun rentalDao(): RentalDao
    abstract fun slotDao(): SlotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "use_tool_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}