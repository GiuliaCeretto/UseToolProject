package com.example.usetool.data.service

import android.content.Context
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.network.FirebaseProvider
import com.example.usetool.data.repository.*
import com.example.usetool.data.AppDatabase

object Injection {
    private var database: AppDatabase? = null

    // Da chiamare in MainActivity
    fun init(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }

    private fun getDb() = database ?: throw IllegalStateException("Injection non inizializzato!")

    fun provideInventoryRepository() = InventoryRepository(
        dataSource = DataSource(FirebaseProvider()),
        toolDao = getDb().toolDao(),
        slotDao = getDb().slotDao(),
        lockerDao = getDb().lockerDao()
    )

    fun provideCartRepository() = CartRepository(
        dataSource = DataSource(FirebaseProvider()),
        cartDao = getDb().cartDao(),
        toolDao = getDb().toolDao()
    )

    fun provideUserRepository() = UserRepository(
        dataSource = DataSource(FirebaseProvider()),
        userDao = getDb().userDao()
    )

    fun provideOrderRepository() = OrderRepository(
        dataSource = DataSource(FirebaseProvider()),
        purchaseDao = getDb().purchaseDao(),
        rentalDao = getDb().rentalDao(),
        cartDao = getDb().cartDao(),
        toolDao = getDb().toolDao(),
        cartRepository = provideCartRepository()
    )

    fun provideExpertRepository() = ExpertRepository(
        dataSource = DataSource(FirebaseProvider()),
        expertDao = getDb().expertDao()
    )
}