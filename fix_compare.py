import os
import re

calc_code = """package com.loanmaster.pro.domain.calculator

import com.loanmaster.pro.domain.model.LoanOffer
import kotlin.math.pow

data class LoanComparisonResult(
    val processedLoans: List<LoanOffer> = emptyList(),
    val hasValidInput: Boolean = false
)

class LoanComparisonCalculator {
    fun calculate(
        loanA: LoanOffer,
        loanB: LoanOffer
    ): LoanComparisonResult {
        val currentLoans = listOf(loanA, loanB)
        
        val hasValidInput = currentLoans.all { it.loanAmount > 0.0 && it.interestRate > 0.0 && it.totalMonths > 0 }

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
        
        val processedLoans = currentLoans.map { loan ->
            loan.copy(isBest = bestLoanIds.contains(loan.id))
        }

        return LoanComparisonResult(
            processedLoans = processedLoans,
            hasValidInput = hasValidInput
        )
    }

    private fun calculateEmi(principal: Double, interestRatePa: Double, totalMonths: Int): Double {
        if (principal <= 0 || totalMonths <= 0) return 0.0
        val r = (interestRatePa / 12) / 100
        if (r == 0.0) return principal / totalMonths
        return principal * (r * (1 + r).pow(totalMonths)) / ((1 + r).pow(totalMonths) - 1)
    }
}
"""

with open("app/src/main/java/com/loanmaster/pro/domain/calculator/LoanComparisonCalculator.kt", "w") as f:
    f.write(calc_code)

vm_path = "app/src/main/java/com/loanmaster/pro/feature/compare/LoanComparisonViewModel.kt"
with open(vm_path, "r") as f:
    vm_content = f.read()

new_vm = """class LoanComparisonViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoanComparisonUiState())
    val uiState: StateFlow<LoanComparisonUiState> = _uiState.asStateFlow()

    private val calculator = LoanComparisonCalculator()

    init {
        updateCalculations()
    }

    fun onEvent(event: LoanComparisonEvent) {
        when (event) {
            is LoanComparisonEvent.UpdateLoanA -> {
                _uiState.update { 
                    it.copy(
                        loanA = it.loanA.copy(
                            amountText = event.amount ?: it.loanA.amountText,
                            interestText = event.interest ?: it.loanA.interestText,
                            yearsText = event.years ?: it.loanA.yearsText,
                            monthsText = event.months ?: it.loanA.monthsText
                        ),
                        showResults = false
                    ) 
                }
                updateCalculations()
            }
            is LoanComparisonEvent.UpdateLoanB -> {
                _uiState.update { 
                    it.copy(
                        loanB = it.loanB.copy(
                            amountText = event.amount ?: it.loanB.amountText,
                            interestText = event.interest ?: it.loanB.interestText,
                            yearsText = event.years ?: it.loanB.yearsText,
                            monthsText = event.months ?: it.loanB.monthsText
                        ),
                        showResults = false
                    ) 
                }
                updateCalculations()
            }
            LoanComparisonEvent.ShowResults -> {
                _uiState.update { it.copy(showResults = true) }
            }
            LoanComparisonEvent.Reset -> {
                _uiState.update {
                    it.copy(
                        loanA = LoanOptionState("A", "Loan A", "", "", "", ""),
                        loanB = LoanOptionState("B", "Loan B", "", "", "", ""),
                        showResults = false
                    )
                }
                updateCalculations()
            }
        }
    }

    private fun updateCalculations() {
        val currentState = _uiState.value
        val loanA = currentState.loanA
        val loanB = currentState.loanB
        
        val offerA = LoanOffer(loanA.id, loanA.bankName, loanA.interestRate, loanA.tenureYears, loanA.tenureMonths, loanA.loanAmount, 0.0, 0.0)
        val offerB = LoanOffer(loanB.id, loanB.bankName, loanB.interestRate, loanB.tenureYears, loanB.tenureMonths, loanB.loanAmount, 0.0, 0.0)
        
        val result = calculator.calculate(offerA, offerB)
        
        _uiState.update {
            it.copy(
                processedLoans = result.processedLoans,
                hasValidInput = result.hasValidInput
            )
        }
    }
}
"""

vm_content = re.sub(r'class LoanComparisonViewModel : ViewModel\(\) \{.*', new_vm, vm_content, flags=re.DOTALL)
vm_content = vm_content.replace("import kotlinx.coroutines.flow.*", "import kotlinx.coroutines.flow.*\nimport com.loanmaster.pro.domain.calculator.LoanComparisonCalculator\nimport com.loanmaster.pro.domain.model.LoanOffer")

with open(vm_path, "w") as f:
    f.write(vm_content)

