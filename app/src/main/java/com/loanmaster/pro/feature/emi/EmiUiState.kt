package com.loanmaster.pro.feature.emi

import com.loanmaster.pro.model.MonthlyAmortization
import com.loanmaster.pro.model.YearBreakdown
import com.loanmaster.pro.model.SmartAlert
import com.loanmaster.pro.model.SmartOpportunity
import com.loanmaster.pro.model.SmartRecommendation

data class EmiUiState(
    val loanAmountText: String = "",
    val interestRateText: String = "",
    val tenureInputText: String = "",
    val isTenureInMonths: Boolean = false,
    val loanType: String = "Home Loan",
    
    val parsedLoanAmount: Double = 0.0,
    val parsedInterestRate: Double = 0.0,
    val parsedTenureYears: Int = 0,
    val totalMonths: Int = 0,
    val hasValidInput: Boolean = false,
    
    val monthlyEmi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalPayment: Double = 0.0,
    val principalPercentage: Double = 0.0,
    val interestPercentage: Double = 0.0,
    
    val monthlySchedule: List<MonthlyAmortization> = emptyList(),
    val yearBreakdown: List<YearBreakdown> = emptyList(),
    
    val recommendations: List<SmartRecommendation> = emptyList(),
    val alerts: List<SmartAlert> = emptyList(),
    val opportunities: List<SmartOpportunity> = emptyList(),
    
    val currentHistoryId: Int = 0,
    val isSavedSuccessfully: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
