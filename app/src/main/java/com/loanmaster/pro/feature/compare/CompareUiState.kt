package com.loanmaster.pro.feature.compare
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

data class LoanOptionState(
    val id: String,
    val bankName: String,
    val amountText: String = "1000000",
    val interestText: String = "8.5",
    val yearsText: String = "5",
    val monthsText: String = "0"
) {
    val loanAmount: Double get() = amountText.toDoubleOrNull() ?: 0.0
    val interestRate: Double get() = interestText.toDoubleOrNull() ?: 0.0
    val tenureYears: Int get() = yearsText.toIntOrNull() ?: 0
    val tenureMonths: Int get() = monthsText.toIntOrNull() ?: 0
    val totalMonths: Int get() = tenureYears * 12 + tenureMonths
}

data class ProcessedLoanOption(
    val offer: LoanOffer,
    val totalCostPer1L: Double,
    val isBest: Boolean
)

data class CompareUiState(
    val loanA: LoanOptionState = LoanOptionState("A", "Loan A", "1000000", "8.5", "5", "0"),
    val loanB: LoanOptionState = LoanOptionState("B", "Loan B", "1000000", "8.0", "5", "0"),
    val showResults: Boolean = false,
    val processedLoans: List<LoanOffer> = emptyList(),
    val hasValidInput: Boolean = false
)

sealed class LoanComparisonEvent {
    data class UpdateLoanA(val amount: String? = null, val interest: String? = null, val years: String? = null, val months: String? = null) : LoanComparisonEvent()
    data class UpdateLoanB(val amount: String? = null, val interest: String? = null, val years: String? = null, val months: String? = null) : LoanComparisonEvent()
    object ShowResults : LoanComparisonEvent()
    object Reset : LoanComparisonEvent()
}
