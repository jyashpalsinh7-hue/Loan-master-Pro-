package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import com.loanmaster.pro.model.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlin.math.pow

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

data class LoanComparisonUiState(
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

class LoanComparisonViewModel : ViewModel() {
    private val _loanA = MutableStateFlow(LoanOptionState("A", "Loan A", "1000000", "8.5", "5", "0"))
    private val _loanB = MutableStateFlow(LoanOptionState("B", "Loan B", "1000000", "8.0", "5", "0"))
    private val _showResults = MutableStateFlow(false)

    val uiState: StateFlow<LoanComparisonUiState> = combine(
        _loanA, _loanB, _showResults
    ) { loanA, loanB, showRes ->
        
        val currentLoans = listOf(
            LoanOffer(loanA.id, loanA.bankName, loanA.interestRate, loanA.tenureYears, loanA.tenureMonths, loanA.loanAmount, 0.0, 0.0),
            LoanOffer(loanB.id, loanB.bankName, loanB.interestRate, loanB.tenureYears, loanB.tenureMonths, loanB.loanAmount, 0.0, 0.0)
        )
        
        var processedLoans = emptyList<LoanOffer>()
        if (showRes) {
            val mapped = currentLoans.map { loan ->
                val loanAmountSafe = if (loan.loanAmount > 0.0) loan.loanAmount else 1.0
                val emi = calculateEmi(loanAmountSafe, loan.interestRate, loan.totalMonths)
                val totalPayment = (emi * loan.totalMonths) + loan.processingFee
                val totalCostPer1L = (totalPayment / loanAmountSafe) * 100000.0
                Pair(loan, totalCostPer1L)
            }
            
            val validMapped = mapped.filter { it.first.loanAmount > 0.0 && it.first.interestRate > 0.0 && it.first.totalMonths > 0 }
            val minCost = validMapped.minOfOrNull { it.second }
            val bestLoanIds = if (minCost != null) {
                validMapped.filter { Math.abs(it.second - minCost) < 1.0 }.map { it.first.id }.toSet()
            } else emptySet()
            
            processedLoans = currentLoans.map { loan ->
                loan.copy(isBest = bestLoanIds.contains(loan.id))
            }
        }
        
        val hasValidInput = currentLoans.all { it.loanAmount > 0.0 && it.interestRate > 0.0 && it.totalMonths > 0 }
        
        LoanComparisonUiState(
            loanA = loanA,
            loanB = loanB,
            showResults = showRes,
            processedLoans = processedLoans,
            hasValidInput = hasValidInput
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoanComparisonUiState()
    )

    fun onEvent(event: LoanComparisonEvent) {
        when (event) {
            is LoanComparisonEvent.UpdateLoanA -> {
                _loanA.update { 
                    it.copy(
                        amountText = event.amount ?: it.amountText,
                        interestText = event.interest ?: it.interestText,
                        yearsText = event.years ?: it.yearsText,
                        monthsText = event.months ?: it.monthsText
                    ) 
                }
                _showResults.value = false
            }
            is LoanComparisonEvent.UpdateLoanB -> {
                _loanB.update { 
                    it.copy(
                        amountText = event.amount ?: it.amountText,
                        interestText = event.interest ?: it.interestText,
                        yearsText = event.years ?: it.yearsText,
                        monthsText = event.months ?: it.monthsText
                    ) 
                }
                _showResults.value = false
            }
            LoanComparisonEvent.ShowResults -> _showResults.value = true
            LoanComparisonEvent.Reset -> {
                _loanA.value = LoanOptionState("A", "Loan A", "", "", "", "")
                _loanB.value = LoanOptionState("B", "Loan B", "", "", "", "")
                _showResults.value = false
            }
        }
    }
    
    private fun calculateEmi(principal: Double, interestRatePa: Double, totalMonths: Int): Double {
        if (principal <= 0 || totalMonths <= 0) return 0.0
        val r = (interestRatePa / 12) / 100
        if (r == 0.0) return principal / totalMonths
        return principal * (r * (1 + r).pow(totalMonths)) / ((1 + r).pow(totalMonths) - 1)
    }
}
