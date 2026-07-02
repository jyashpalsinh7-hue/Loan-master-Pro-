package com.example

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CalculationHistory::class, ActiveLoan::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun activeLoanDao(): ActiveLoanDao
}
