package com.loanmaster.pro.feature.prepayment
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

data class PrepaymentCalculatorUiState(
    val loanAmountText: String = "",
    val interestRateText: String = "",
    val tenureYearsText: String = "",
    val prepaymentAmountText: String = "",
    val strategy: String = "Tenure", // "Tenure" or "EMI"
    val monthlyPrepaymentText: String = "0",
    val annualPrepaymentText: String = "0",
    val currentHistoryId: Int = 0,
    
    // Calculated
    val originalEmi: Double = 0.0,
    val originalTotalPayment: Double = 0.0,
    val originalTotalInterest: Double = 0.0,
    
    val newEmi: Double = 0.0,
    val newTenureMonths: Double = 0.0,
    val newTotalInterest: Double = 0.0,
    
    val interestSaved: Double = 0.0,
    val tenureReducedMonths: Double = 0.0,
    val emiReduced: Double = 0.0,
    
    val hasValidInput: Boolean = false,
    val standardSchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow> = emptyList(),
    val prepaySchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow> = emptyList()
)

sealed class PrepaymentEvent {
    data class LoanAmountChanged(val amount: String) : PrepaymentEvent()
    data class InterestRateChanged(val rate: String) : PrepaymentEvent()
    data class TenureChanged(val years: String) : PrepaymentEvent()
    data class PrepaymentAmountChanged(val amount: String) : PrepaymentEvent()
    data class StrategyChanged(val strategy: String) : PrepaymentEvent()
    data class MonthlyPrepaymentChanged(val amount: String) : PrepaymentEvent()
    data class AnnualPrepaymentChanged(val amount: String) : PrepaymentEvent()
    data class InitializeFromHistory(val history: CalculationHistory) : PrepaymentEvent()
    data class HistoryIdUpdated(val id: Int) : PrepaymentEvent()
}
