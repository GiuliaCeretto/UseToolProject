package com.example.usetool.data.service

import android.content.Context
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.network.FirebaseProvider
import com.example.usetool.data.repository.*
import com.example.usetool.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth

object Injection {
    private var database: AppDatabase? = null

    // --- SINGLETON CACHE ---
    // Queste variabili servono a memorizzare le istanze per non ricrearle mai più
    private var dataSource: DataSource? = null
    private var inventoryRepository: InventoryRepository? = null
    private var cartRepository: CartRepository? = null
    private var userRepository: UserRepository? = null
    private var orderRepository: OrderRepository? = null
    private var expertRepository: ExpertRepository? = null

    fun init(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }

    private fun getDb() = database ?: throw IllegalStateException("Injection non inizializzato!")

    // Creiamo un unico DataSource per tutta l'app
    private fun provideDataSource() = dataSource ?: DataSource(FirebaseProvider()).also {
        dataSource = it
    }

    // --- REPOSITORY PROVIDERS ---

    fun provideInventoryRepository() = inventoryRepository ?: InventoryRepository(
        dataSource = provideDataSource(),
        toolDao = getDb().toolDao(),
        slotDao = getDb().slotDao(),
        lockerDao = getDb().lockerDao()
    ).also { inventoryRepository = it }

    fun provideCartRepository() = cartRepository ?: CartRepository(
        dataSource = provideDataSource(),
        cartDao = getDb().cartDao(),
        toolDao = getDb().toolDao()
    ).also { cartRepository = it }

    fun provideUserRepository() = userRepository ?: UserRepository(
        dataSource = provideDataSource(),
        userDao = getDb().userDao(),
        firebaseAuth = FirebaseAuth.getInstance()
    ).also { userRepository = it }

    fun provideOrderRepository() = orderRepository ?: OrderRepository(
        dataSource = provideDataSource(),
        purchaseDao = getDb().purchaseDao(),
        rentalDao = getDb().rentalDao(),
        cartDao = getDb().cartDao(),
        toolDao = getDb().toolDao(),
        cartRepository = provideCartRepository()
    ).also { orderRepository = it }

    fun provideExpertRepository() = expertRepository ?: ExpertRepository(
        dataSource = provideDataSource(),
        expertDao = getDb().expertDao()
    ).also { expertRepository = it }
}