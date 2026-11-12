package com.example.foodscanner.data.repository

import com.example.foodscanner.data.dao.ScanHistoryDao
import com.example.foodscanner.data.entity.ScanHistory
import kotlinx.coroutines.flow.Flow

class ScanHistoryRepository(private val scanHistoryDao: ScanHistoryDao) {

    suspend fun insertScan(scan: ScanHistory) {
        scanHistoryDao.insertScan(scan)
    }

    fun getUserScans(userId: String): Flow<List<ScanHistory>> {
        return scanHistoryDao.getUserScans(userId)
    }

    suspend fun getRecentScans(userId: String, limit: Int = 5): List<ScanHistory> {
        return scanHistoryDao.getRecentScans(userId, limit)
    }

    suspend fun deleteScan(scanId: Long) {
        scanHistoryDao.deleteScan(scanId)
    }
}
