package com.loanmaster.pro.domain.calculator

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
