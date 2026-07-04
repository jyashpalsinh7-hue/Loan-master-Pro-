package com.loanmaster.pro

import kotlinx.coroutines.flow.Flow
import android.util.Log

open class HistoryRepository(private val historyDao: HistoryDao) {
    
    open fun getAllHistory(): Flow<List<CalculationHistory>> {
        return historyDao.getAllHistory()
    }
    
    fun getHistoryByType(type: String): Flow<List<CalculationHistory>> {
        return historyDao.getHistoryByType(type)
    }
    
    open suspend fun saveHistory(history: CalculationHistory): Long {
        return try {
            historyDao.insertHistory(history)
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error saving history", e)
            -1L
        }
    }
    
    suspend fun deleteHistory(history: CalculationHistory) {
        try {
            historyDao.deleteHistory(history)
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error deleting history", e)
        }
    }
    
    open suspend fun deleteAllHistory() {
        try {
            historyDao.deleteAllHistory()
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error deleting all history", e)
        }
    }

    // Maintained for backward compatibility with existing view models
    
    open suspend fun deleteById(id: Int) {
        try {
            historyDao.deleteHistoryById(id)
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error deleting history by id", e)
        }
    }
}
