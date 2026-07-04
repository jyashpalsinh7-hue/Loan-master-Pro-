package com.loanmaster.pro

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistory>>
    
    @Query("SELECT * FROM calculation_history WHERE calculatorType = :type ORDER BY timestamp DESC")
    fun getHistoryByType(type: String): Flow<List<CalculationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CalculationHistory): Long

    @Delete
    suspend fun deleteHistory(history: CalculationHistory)
    
    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)
    
    @Query("DELETE FROM calculation_history")
    suspend fun deleteAllHistory()
    
    @Query("DELETE FROM calculation_history")
    suspend fun clearAllHistory()
}
