package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_loans")
data class ActiveLoan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bankName: String,
    val loanType: String,
    val principalAmount: Double,
    val interestRate: Double,
    val tenureMonths: Int,
    val emiAmount: Double,
    val startDate: Long,
    val totalInterest: Double = 0.0
)
