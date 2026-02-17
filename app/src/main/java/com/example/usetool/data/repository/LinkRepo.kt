package com.example.usetool.data.repository

import com.example.usetool.data.dao.LinkDao
import com.example.usetool.data.dao.LinkEntity
import com.example.usetool.data.network.DataSource
import kotlinx.coroutines.flow.Flow

class LinkRepo(
    private val dataSource: DataSource,
    private val linkDao: LinkDao
) {
    val allLogs: Flow<List<LinkEntity>> = linkDao.getAllLinks()

    suspend fun logConnection(lockerLinkId: Int, userId: Int) {
        val newLink = LinkEntity(
            lockerId = lockerLinkId,
            userId = userId,
            connectionTime = System.currentTimeMillis().toString()
        )

        linkDao.insertLink(newLink)
    }

    suspend fun clearHistory() {
        linkDao.deleteAll()
    }
}