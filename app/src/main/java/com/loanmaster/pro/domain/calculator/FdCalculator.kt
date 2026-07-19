package com.loanmaster.pro.domain.calculator

import com.loanmaster.pro.domain.model.FdYearBreakdown
import kotlin.math.ceil
import kotlin.math.pow

enum class CompoundingFrequency(val displayName: String, val periods: Int) {
    YEARLY("Yearly", 1),
    HALF_YEARLY("Half-Yearly", 2),
    QUARTERLY("Quarterly", 4),
    MONTHLY("Monthly", 12)
}

data class FdResult(
    val maturityValue: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalInterest: Double = 0.0,
    val wealthGain: Double = 0.0,
    val breakdown: List<FdYearBreakdown> = emptyList(),
    val isValid: Boolean = false
)

class FdCalculator {
    fun calculate(
        deposit: String,
        rate: String,
        tenureYears: String,
        frequency: CompoundingFrequency
    ): FdResult {
        val parsedP = deposit.toDoubleOrNull()
        val parsedR = rate.toDoubleOrNull()
        val parsedT = tenureYears.toDoubleOrNull()

        if (parsedP == null || parsedR == null || parsedT == null) {
            return FdResult(isValid = false)
        }

        if (parsedP <= 0.0 || parsedR <= 0.0 || parsedT <= 0.0) {
            return FdResult(isValid = false)
        }

        val p: Double = parsedP
        val r: Double = parsedR
        val t: Double = parsedT

        val annualRate = r / 100.0
        val n = frequency.periods.toDouble()

        val maturity = p * (1 + annualRate / n).pow(n * t)
        val totalInterest = maturity - p
        val wealthGain = if (p > 0) (totalInterest / p) * 100 else 0.0

        val maxYears = ceil(t).toInt()
        var currentBal = p
        val breakdown = (1..maxYears).map { y ->
            val actualYears = if (y > t && y - 1 < t) t else y.toDouble()
            val tMat = p * (1 + annualRate / n).pow(n * actualYears)
            val interestEarned = tMat - currentBal
            val displayYear = minOf(y.toDouble(), t)
            val breakdownRow = FdYearBreakdown(
                year = displayYear,
                openingBalance = currentBal,
                interestEarned = interestEarned,
                closingBalance = tMat
            )
            currentBal = tMat
            breakdownRow
        }

        if (breakdown.isNotEmpty()) {
            require(
                kotlin.math.abs(
                    breakdown.last().closingBalance - maturity
                ) < 0.01
            )
        }

        return FdResult(
            maturityValue = maturity,
            totalInvested = p,
            totalInterest = totalInterest,
            wealthGain = wealthGain,
            breakdown = breakdown,
            isValid = true
        )
    }
}
