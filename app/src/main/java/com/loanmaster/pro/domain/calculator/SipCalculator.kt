package com.loanmaster.pro.domain.calculator

import com.loanmaster.pro.domain.model.YearlyData
import kotlin.math.pow

data class SipResult(
    val totalInvested: Double = 0.0,
    val totalGain: Double = 0.0,
    val maturityValue: Double = 0.0,
    val yearlyDataList: List<YearlyData> = emptyList(),
    val isValid: Boolean = false,
    val inflationAdjustedValue: Double = 0.0
)

class SipCalculator {
    fun calculate(
        amount: String,
        rate: String,
        years: String,
        stepUp: String
    ): SipResult {
        val p = amount.toDoubleOrNull() ?: 0.0
        val r = rate.toDoubleOrNull() ?: 0.0
        val y = years.toIntOrNull() ?: 0
        val s = stepUp.toDoubleOrNull() ?: 0.0

        if (p <= 0 || r <= 0 || y <= 0) {
            return SipResult(isValid = false)
        }

        var totalInvested = 0.0
        var maturityValue = 0.0
        var currentMonthlySip = p
        val monthlyReturnRate = (r / 100.0) / 12.0
        val totalMonths = y * 12
        val stepUpFraction = s / 100.0
        
        val yearlyDataList = mutableListOf<YearlyData>()
        var investedThisYear = 0.0
        
        for (m in 1..totalMonths) {
            totalInvested += currentMonthlySip
            investedThisYear += currentMonthlySip
            maturityValue = (maturityValue + currentMonthlySip) * (1 + monthlyReturnRate)
            if (m % 12 == 0) {
                val year = m / 12
                yearlyDataList.add(
                    YearlyData(
                        year = year,
                        investedForYear = investedThisYear,
                        totalInvested = totalInvested,
                        returns = maturityValue - totalInvested,
                        maturity = maturityValue
                    )
                )
                investedThisYear = 0.0
                currentMonthlySip += currentMonthlySip * stepUpFraction
            }
        }
        
        val totalGain = maturityValue - totalInvested

        val inflationRate = 0.06
        val inflationAdjustedValue = maturityValue / (1 + inflationRate).pow(y.toDouble())
        return SipResult(
            totalInvested = totalInvested,
            totalGain = totalGain,
            maturityValue = maturityValue,
            yearlyDataList = yearlyDataList,
            isValid = true,
            inflationAdjustedValue = inflationAdjustedValue
        )
    }
}
