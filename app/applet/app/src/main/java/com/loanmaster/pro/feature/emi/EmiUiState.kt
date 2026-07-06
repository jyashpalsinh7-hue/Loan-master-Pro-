package com.loanmaster.pro.feature.emi

data class EmiUiState(
    val loanAmountText: String = "",
    val interestRateText: String = "",
    val tenureYearsText: String = "",
    val monthlyEmi: Double = 0.0,
    val totalInterest: Double = 0.0,
    val totalPayment: Double = 0.0,
    val hasValidInput: Boolean = false
)
