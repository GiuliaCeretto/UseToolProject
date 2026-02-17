package com.example.usetool.data.service

import android.content.Context
import com.example.usetool.data.AppDatabase
import com.example.usetool.data.network.DataSource
import com.example.usetool.data.network.FirebaseProvider
import com.example.usetool.data.repository.*
import com.google.firebase.auth.FirebaseAuth

object Injection {
    private var database: AppDatabase? = null

    // --- SINGLETON CACHE ---
    private var dataSource: DataSource? = null
    private var inventoryRepository: InventoryRepository? = null
    private var cartRepository: CartRepository? = null
    private var userRepository: UserRepository? = null
    private var orderRepository: OrderRepository? = null
    private var expertRepository: ExpertRepository? = null
    private var linkRepo: LinkRepo? = null

    fun init(context: Context) {
        if (database == null) {
            database = AppDatabase.getDatabase(context)
        }
    }

    private fun getDb() = database ?: throw IllegalStateException("Injection non inizializzato! Chiama Injection.init(context) nell'Application class o nella MainActivity.")

    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    private fun provideDataSource(): DataSource {
        return dataSource ?: run {
            val fbProvider = FirebaseProvider()
            val newDataSource = DataSource(fbProvider)
            dataSource = newDataSource
            newDataSource
        }
    }

    fun provideLinkRepo() = linkRepo ?: LinkRepo(
        dataSource = provideDataSource(),
        linkDao = getDb().linkDao()
    ).also { linkRepo = it }

    fun provideInventoryRepository() = inventoryRepository ?: InventoryRepository(
        dataSource = provideDataSource(),
        toolDao = getDb().toolDao(),
        slotDao = getDb().slotDao(),
        lockerDao = getDb().lockerDao(),
        cartDao = getDb().cartDao()
    ).also { inventoryRepository = it }

    fun provideCartRepository() = cartRepository ?: CartRepository(
        dataSource = provideDataSource(),
        cartDao = getDb().cartDao(),
        toolDao = getDb().toolDao(),
        slotDao = getDb().slotDao(),
        lockerDao = getDb().lockerDao()
    ).also { cartRepository = it }

    fun provideUserRepository() = userRepository ?: UserRepository(
        dataSource = provideDataSource(),
        userDao = getDb().userDao(),
        firebaseAuth = provideFirebaseAuth()
    ).also { userRepository = it }

    fun provideOrderRepository() = orderRepository ?: OrderRepository(
        dataSource = provideDataSource(),
        purchaseDao = getDb().purchaseDao(),
        rentalDao = getDb().rentalDao(),
        cartDao = getDb().cartDao(),
        toolDao = getDb().toolDao(),
        slotDao = getDb().slotDao(),
        cartRepository = provideCartRepository()
    ).also { orderRepository = it }

    fun provideExpertRepository() = expertRepository ?: ExpertRepository(
        dataSource = provideDataSource(),
        expertDao = getDb().expertDao()
    ).also { expertRepository = it }
}