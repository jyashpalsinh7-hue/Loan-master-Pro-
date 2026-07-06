package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import kotlinx.coroutines.flow.Flow

class ActiveLoanRepository(private val activeLoanDao: ActiveLoanDao) {
    fun getAllActiveLoans(): Flow<List<ActiveLoan>> = activeLoanDao.getAllActiveLoans()

    suspend fun insertActiveLoan(loan: ActiveLoan) {
        activeLoanDao.insertActiveLoan(loan)
    }

    suspend fun deleteActiveLoan(loan: ActiveLoan) {
        activeLoanDao.deleteActiveLoan(loan)
    }
}
