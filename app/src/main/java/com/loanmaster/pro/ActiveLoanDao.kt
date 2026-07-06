package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveLoanDao {
    @Query("SELECT * FROM active_loans ORDER BY startDate DESC")
    fun getAllActiveLoans(): Flow<List<ActiveLoan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActiveLoan(loan: ActiveLoan)

    @Delete
    suspend fun deleteActiveLoan(loan: ActiveLoan)
}
