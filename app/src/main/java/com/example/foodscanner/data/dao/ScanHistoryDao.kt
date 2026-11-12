package com.example.foodscanner.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.foodscanner.data.entity.ScanHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert
    suspend fun insertScan(scan: ScanHistory)

    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY scanDate DESC")
    fun getUserScans(userId: String): Flow<List<ScanHistory>>

    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY scanDate DESC LIMIT :limit")
    suspend fun getRecentScans(userId: String, limit: Int): List<ScanHistory>

    @Query("DELETE FROM scan_history WHERE id = :scanId")
    suspend fun deleteScan(scanId: Long)

    @Query("DELETE FROM scan_history WHERE userId = :userId")
    suspend fun deleteAllUserScans(userId: String)
}
