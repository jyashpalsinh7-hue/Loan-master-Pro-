package com.example

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()

    suspend fun insert(history: CalculationHistory): Long {
        return historyDao.insertHistory(history)
    }

    suspend fun deleteById(id: Int) {
        historyDao.deleteHistoryById(id)
    }
    
    suspend fun clearAll() {
        historyDao.clearAllHistory()
    }
}
