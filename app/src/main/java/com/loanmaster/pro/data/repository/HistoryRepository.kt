package com.loanmaster.pro.data.repository

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.settings.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

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
