package com.example.foodscanner.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scan_history")
data class ScanHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val barcode: String,
    val productName: String?,
    val brand: String?,
    val imageUrl: String?,
    val nutriScore: String?,
    val quantity: String?,
    val scanDate: Long = System.currentTimeMillis()
)
