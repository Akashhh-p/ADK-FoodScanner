package com.example.foodscanner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodscanner.data.dao.ScanHistoryDao
import com.example.foodscanner.data.entity.ScanHistory

@Database(entities = [ScanHistory::class], version = 1, exportSchema = false)
abstract class FoodScannerDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: FoodScannerDatabase? = null

        fun getDatabase(context: Context): FoodScannerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodScannerDatabase::class.java,
                    "food_scanner_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
