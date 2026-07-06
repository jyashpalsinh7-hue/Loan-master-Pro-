package com.loanmaster.pro

import com.loanmaster.pro.ui.theme.*

import kotlin.math.ceil
import kotlin.math.pow

enum class CompoundingFrequency(val displayName: String, val periods: Int) {
    YEARLY("Yearly", 1),
    HALF_YEARLY("Half-Yearly", 2),
    QUARTERLY("Quarterly", 4),
    MONTHLY("Monthly", 12)
}

data class FdResult(
    val maturityValue: Double,
    val totalInvested: Double,
    val totalReturns: Double,
    val wealthGain: Double,
    val breakdown: List<FdYearBreakdown>
)

data class FdYearBreakdown(
    val year: Int,
    val deposit: Double,
    val interest: Double,
    val maturity: Double
)

object FdCalculator {
    fun calculate(
        principal: Double,
        rate: Double,
        tenureYears: Double,
        frequency: CompoundingFrequency
    ): FdResult {
        if (principal <= 0 || rate <= 0 || tenureYears <= 0) {
            return FdResult(0.0, 0.0, 0.0, 0.0, emptyList())
        }

        val ratePerPeriod = rate / 100
        val n = frequency.periods.toDouble()
        val maturity = principal * (1 + ratePerPeriod / n).pow(n * tenureYears)
        val returns = maturity - principal
        val wealthGain = if (principal > 0) (returns / principal) * 100 else 0.0

        val maxYears = ceil(tenureYears).toInt()
        val breakdown = (1..maxYears).map { y ->
            val tMat = principal * (1 + ratePerPeriod / n).pow(n * y.toDouble())
            FdYearBreakdown(y, principal, tMat - principal, tMat)
        }

        return FdResult(
            maturityValue = maturity,
            totalInvested = principal,
            totalReturns = returns,
            wealthGain = wealthGain,
            breakdown = breakdown
        )
    }
}
