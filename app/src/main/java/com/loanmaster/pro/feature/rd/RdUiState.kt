package com.loanmaster.pro.feature.rd
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

data class RdUiState(
    val selectedTab: String = "Standard",
    val monthlyDepositText: String = "",
    val interestRatePaText: String = "",
    val tenureYearsText: String = "",
    val compoundingFrequency: String = "Quarterly",
    val targetAmountText: String = "",
    val currentHistoryId: Int = 0,
    val maturityValue: Double = 0.0,
    val calculatedMonthlyDeposit: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalReturns: Double = 0.0,
    val wealthGain: Double = 0.0,
    val hasValidInput: Boolean = false
)
