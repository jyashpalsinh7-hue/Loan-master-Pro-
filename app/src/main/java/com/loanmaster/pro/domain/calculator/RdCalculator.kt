package com.loanmaster.pro.domain.calculator

import kotlin.math.pow

data class RdResult(
    val maturityValue: Double = 0.0,
    val calculatedMonthlyDeposit: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalReturns: Double = 0.0,
    val wealthGain: Double = 0.0,
    val isValid: Boolean = false
)

class RdCalculator {
    fun calculate(
        tab: String,
        depositStr: String,
        rateStr: String,
        tenureStr: String,
        compFreq: String,
        targetStr: String
    ): RdResult {
        val deposit = depositStr.toDoubleOrNull() ?: 0.0
        val target = targetStr.toDoubleOrNull() ?: 0.0
        val rate = rateStr.toDoubleOrNull() ?: 0.0
        val years = tenureStr.toDoubleOrNull()?.coerceIn(0.0, 100.0) ?: 0.0
                
        val annualRate = rate / 100
        val months = (years * 12).toInt()
                
        val n = when (compFreq) {
            "Yearly" -> 1.0
            "Half-Yearly" -> 2.0
            "Quarterly" -> 4.0
            "Monthly" -> 12.0
            else -> 4.0
        }
        
        var maturityValue = 0.0
        var calculatedMonthlyDeposit = 0.0
                
        val isValid = if (tab == "Standard") {
            deposit > 0 && rate > 0 && years > 0
        } else {
            target > 0 && rate > 0 && years > 0
        }

        if (!isValid) {
            return RdResult(isValid = false)
        }

        if (tab == "Standard") {
            calculatedMonthlyDeposit = deposit
            if (annualRate > 0 && months > 0) {
                for (i in 1..months) {
                    val remainingTimeYears = (months - i + 1) / 12.0
                    maturityValue += deposit * (1 + annualRate / n).pow(n * remainingTimeYears)
                }
            } else {
                maturityValue = deposit * months
            }
        } else {
            maturityValue = target
            if (annualRate > 0 && months > 0) {
                var sumFactors = 0.0
                for (i in 1..months) {
                    val remainingTimeYears = (months - i + 1) / 12.0
                    sumFactors += (1 + annualRate / n).pow(n * remainingTimeYears)
                }
                if (sumFactors > 0) {
                    calculatedMonthlyDeposit = target / sumFactors
                }
            } else if (months > 0) {
                calculatedMonthlyDeposit = target / months.toDouble()
            }
        }
        
        val totalInvested = calculatedMonthlyDeposit * months
        val totalReturns = if (maturityValue > totalInvested) maturityValue - totalInvested else 0.0
        val wealthGain = if (totalInvested > 0) (totalReturns / totalInvested) * 100 else 0.0

        return RdResult(
            maturityValue = maturityValue,
            calculatedMonthlyDeposit = calculatedMonthlyDeposit,
            totalInvested = totalInvested,
            totalReturns = totalReturns,
            wealthGain = wealthGain,
            isValid = isValid
        )
    }
}
