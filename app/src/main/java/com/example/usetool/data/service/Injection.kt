package com.example.usetool.data.service

import android.content.Context
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.network.FirebaseProvider
import com.example.usetool.data.repository.*
import com.example.usetool.data.AppDatabase

/**
 * Questa classe risolve i warning di "unused" in AppDatabase perché
 * richiama attivamente getDatabase() e tutti i DAO per istruire i Repository.
 */
object Injection {

    // Fornisce il DataSource (che a sua volta usa il FirebaseProvider)
    private fun provideDataSource() = DataSource(FirebaseProvider())

    // RISOLTO: Qui usiamo getDatabase, toolDao, slotDao e lockerDao
    fun provideInventoryRepository(context: Context): InventoryRepository {
        val db = AppDatabase.getDatabase(context)
        return InventoryRepository(
            dataSource = provideDataSource(),
            toolDao = db.toolDao(),
            slotDao = db.slotDao(),
            lockerDao = db.lockerDao()
        )
    }

    // RISOLTO: Qui usiamo cartDao
    fun provideCartRepository(context: Context): CartRepository {
        val db = AppDatabase.getDatabase(context)
        return CartRepository(
            dataSource = provideDataSource(),
            cartDao = db.cartDao()
        )
    }

    // RISOLTO: Qui usiamo userDao
    fun provideUserRepository(context: Context): UserRepository {
        val db = AppDatabase.getDatabase(context)
        return UserRepository(
            dataSource = provideDataSource(),
            userDao = db.userDao()
        )
    }

    // RISOLTO: Qui usiamo purchaseDao e rentalDao
    fun provideOrderRepository(context: Context): OrderRepository {
        val db = AppDatabase.getDatabase(context)
        // We must call provideCartRepository to satisfy the dependency
        return OrderRepository(
            dataSource = provideDataSource(),
            purchaseDao = db.purchaseDao(),
            rentalDao = db.rentalDao(),
            cartRepository = provideCartRepository(context) // Fix: Pass the dependency here
        )
    }
    // RISOLTO: Qui usiamo expertDao
    fun provideExpertRepository(context: Context): ExpertRepository {
        val db = AppDatabase.getDatabase(context)
        return ExpertRepository(
            dataSource = provideDataSource(),
            expertDao = db.expertDao()
        )
    }
}