package com.example.usetool.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.usetool.data.dao.*
import com.example.usetool.data.service.Converters

@Database(
    entities = [
        ToolEntity::class,
        UserEntity::class,
        SlotEntity::class,
        CartEntity::class,
        CartItemEntity::class,
        ExpertEntity::class,
        LockerEntity::class,
        PurchaseEntity::class,
        RentalEntity::class,
        LinkEntity::class
    ],
    version = 13,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun toolDao(): ToolDao
    abstract fun userDao(): UserDao
    abstract fun slotDao(): SlotDao
    abstract fun cartDao(): CartDao
    abstract fun expertDao(): ExpertDao
    abstract fun lockerDao(): LockerDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun rentalDao(): RentalDao
    abstract fun linkDao(): LinkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "use_tool_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}