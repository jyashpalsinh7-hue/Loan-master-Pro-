package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CalculationHistory::class, ActiveLoan::class], version = 3, exportSchema = false)
abstract class LoanMasterDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun activeLoanDao(): ActiveLoanDao
}
